# Contributing to Documentation

## Golden Rules

1. Public docs describe **what exists**, not what is planned
2. Every page must target a specific audience
3. Version-specific behaviour must be stated clearly

## Where Things Go

| Content       | Location          |
|---------------|-------------------|
| Player Info   | `/docs/mod/`      |
| API Reference | `/docs/api/`      |
| Architecture  | `/docs/internals` |

## Version Notes

This documentation and mod utilises *
*[Expanded Semantic Versioning](https://docs.neoforged.net/docs/gettingstarted/versioning#reduced-and-expanded-semver)**
utilising the below format:

`major.api.minor.patch`

We also include the Minecraft version, NeoForge tag, and potential version tags for a full version tag of the following:

`dronecraft-1.21.11-v1.2.3.4{-rc, -beta, -experimental, -release}`


> [!Info] To Note
> #### Modding API
> Currently, we only support the NeoForge modding API, but hope to expand in the future, hence why we have including the
> modding API tag in the version
>
> #### Build Tags
>  `-experimental`: Should be used with extreme caution and only used for testing. Likely to break worlds
> `-beta`: Indicates that a build is in beta and can be used for games, but may still cause world breaks. Used by people
> who want to test new features before release
> `-rc`: Indicates a release candidate, which should be the final beta release before promotion to release. No new
> features will be added before release, only bugfixes.
> `-final`: Indicates the final release of the mod for a specific Minecraft version (`1.20`)
>
>  If not build tag is added, it should be treated as a full release

### Versioning in Documentation

If behaviour differs between versions, add (excludes the patch number):

> Introduced in `1.5.0`

or

> Changed in `2.0.1`

---

## ✍️ Style Guidelines

- Prefer examples to theory
- Prefer diagrams to long prose
- Avoid future tense in public docs
- Avoid TODOs in published docs

---

## 🖼 Images & Diagrams

- Images go in `/docs/DroneCraft/assets/`
- Use Mermaid for architecture and flow diagrams
- Keep diagrams simple and readable

---

## 🔄 Promotion Workflow

When a feature stabilises:

1. Remove speculative language
2. Move content from `/docs-internal/`
3. Publish clean documentation in `/docs/`

Public docs should always be **authoritative**.
