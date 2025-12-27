# API Stability Notice

## API Tiers

| Tier         | Description                       |
| ------------ | --------------------------------- |
| Stable       | Guaranteed within a major release |
| Experimental | May change between minor versions |
| Internal     | Not supported for external use    |

## Versioning Rules
- Patch  (`1.4.1.2 -> 1.4.1.3`): No breaking changes
- Minor  (`1.4.1.x -> 1.4.2.x`): No breaking changes
- API      (`1.4.x.x -> 1.5.x.x`): Additive, deprecations allowed
- Major  (`1.5.x.x -> 2.x.x.x`): Breaking changes allowed

## Deprecation
Deprecated APIs will remain for **at least one API version**. All deprecation notices will include the expected removal version.