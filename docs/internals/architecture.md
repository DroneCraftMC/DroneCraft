# Architecture Overview

This section is for contributors and maintainers.

## High-level flow

```mermaid
graph TD
	Player -->|Uses| Block
	Block -->|Triggers| System
	System -->|Updates| WorldState
	WorldState -->|Syncs| Client
```