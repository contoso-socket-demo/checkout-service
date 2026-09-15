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

Measured on the source repo: **~61% Tier 2 and ~82% Tier 1** noise reduction,
42 critical CVEs of which only 3 are genuinely reachable.

Re-verify after the first real scans here. Those numbers are a property of
the scan, not of this file.

## Known analysis limits

A 90% Tier 1 target was attempted on the source repo and landed at 82%. The
gap is real Coana coverage limits on `tomcat-embed-core` and `spring-webmvc`
(`undeterminable_reachability`) plus one failed transitive install
(`xerces:xercesImpl`, which falls back to Tier 2). The
`--reach-continue-on-*` flags in the workflow exist so those fall back
instead of halting the run.
