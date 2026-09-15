# checkout-service

Contoso checkout and order service. Java 17, Maven.

Ported from the tuned `socket-reachability-java` composition rather than
rebuilt, so the measured shape carries over.

## What this repo demos

Tier 1 and Tier 2 reachability side by side. `socket-scans.yml` runs two
scans against the same tree twice a day:

- `socket scan create` — Tier 2, precomputed reachability
- `socket scan create --reach` — Tier 1, Coana call-graph analysis

## The shape, and why it is what it is

**Tier 2 only ever resolves TRANSITIVE dependency CVEs.** A direct dependency
always reports `direct_dependency` and stays permanently unresolved no matter
which package it is. That is a product ceiling, not a tuning knob.

So the composition deliberately parks most CVE volume in the transitive tree
under `spring-boot-starter-web:1.5.10.RELEASE` (jackson-databind 2.8.10,
tomcat-embed-core, hibernate-validator, spring-web/webmvc), while keeping a
small number of genuinely reachable direct packages.

| Group | Packages |
|---|---|
| Wired, reachable | `log4j-core` (Log4Shell), `commons-text` (Text4Shell) |
| Dead-code call paths only | `xstream`, `commons-collections` |
| Declared, never touched | `snakeyaml`, `spring-core`, `guava`, `commons-beanutils`, `commons-fileupload`, `bcprov`/`bcpkix`, `velocity`, `dom4j`, `fastjson` |

Measured on the first real scans in this repo, 2026-09-15, 233 CVE alerts
in both modes:

| Verdict | Tier 2 | Tier 1 |
|---|---|---|
| unreachable | 117 (50.2%) | **166 (71.2%)** |
| `direct_dependency` | 61 (26.2%) | 0 |
| undeterminable | 40 (17.2%) | 40 (17.2%) |
| `maybe_reachable` | 3 (1.3%) | 0 |
| reachable | 0 | **19 (8.2%)** |
| missing_support / pending / error | 12 (5.2%) | 8 (3.4%) |

**Noise reduction: 50.2% Tier 2, 71.2% Tier 1.**

The source repo this was ported from measured ~61% / ~82%. This repo measures
lower. The composition is identical, so the delta is Coana version drift
(15.10.40 here) plus new CVEs published against the same packages since. Use
the numbers above, not the older ones.

**The strongest demo line is the `direct_dependency` row.** Tier 2 leaves 61
CVEs (26.2%) permanently unresolved because it cannot reason about direct
dependencies at all. Tier 1 resolves every one of them, which is the cleanest
observable difference between the two tiers.

Second strongest: Tier 1 confirms only **19 of 233** CVE alerts are actually
reachable.

## Known analysis limits

A 90% Tier 1 target was attempted on the source repo and landed at 82%. The
gap is real Coana coverage limits on `tomcat-embed-core` and `spring-webmvc`
(`undeterminable_reachability`) plus one failed transitive install
(`xerces:xercesImpl`, which falls back to Tier 2). The
`--reach-continue-on-*` flags in the workflow exist so those fall back
instead of halting the run.
