---
name: GitActionBoard
description: A calm, single-signal CI dashboard where color and motion carry all the meaning — nothing is labeled that doesn't have to be.
colors:
  signal-blue: "#7c9eff"
  signal-blue-soft: "rgba(124,158,255,.14)"
  signal-blue-light: "#4a63d6"
  signal-blue-light-soft: "rgba(74,99,214,.08)"
  success-green: "#4fb87a"
  success-green-soft: "rgba(79,184,122,.14)"
  success-green-light: "#1f9d55"
  success-green-light-soft: "rgba(31,157,85,.06)"
  error-coral: "#e2685f"
  error-coral-soft: "rgba(226,104,95,.14)"
  error-coral-light: "#d1483e"
  error-coral-light-soft: "rgba(209,72,62,.06)"
  warning-amber: "#d9a441"
  warning-amber-soft: "rgba(217,164,65,.18)"
  warning-amber-light: "#b8790f"
  warning-amber-light-soft: "rgba(184,121,15,.06)"
  canvas: "#14161a"
  canvas-light: "#f6f7f9"
  surface: "#1c1f26"
  surface-light: "#ffffff"
  surface-raised: "#23262e"
  surface-raised-light: "#f0f1f4"
  border: "#2c303a"
  border-light: "#e4e6ea"
  text: "#e7e9ee"
  text-light: "#1c1f26"
  text-muted: "#9aa1af"
  text-muted-light: "#5b6472"
typography:
  title:
    fontFamily: "Inter Variable, -apple-system, BlinkMacSystemFont, sans-serif"
    fontSize: "20px"
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: "-0.01em"
  headline:
    fontFamily: "Inter Variable, -apple-system, BlinkMacSystemFont, sans-serif"
    fontSize: "15px"
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: "normal"
  body:
    fontFamily: "Inter Variable, -apple-system, BlinkMacSystemFont, sans-serif"
    fontSize: "13.5px"
    fontWeight: 600
    lineHeight: 1.3
    letterSpacing: "normal"
  label:
    fontFamily: "Inter Variable, -apple-system, BlinkMacSystemFont, sans-serif"
    fontSize: "10.5px"
    fontWeight: 600
    lineHeight: 1.3
    letterSpacing: "normal"
  mono-label:
    fontFamily: "JetBrains Mono Variable, monospace"
    fontSize: "10.5px"
    fontWeight: 600
    lineHeight: 1.3
    letterSpacing: "normal"
rounded:
  sm: "4px"
  md: "8px"
  lg: "10px"
  xl: "12px"
  full: "50%"
spacing:
  xs: "6px"
  sm: "10px"
  md: "12px"
  lg: "16px"
  xl: "24px"
  xxl: "32px"
components:
  button-primary:
    backgroundColor: "{colors.signal-blue}"
    textColor: "#ffffff"
    rounded: "{rounded.md}"
    padding: "10px 20px"
  card:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.md}"
    padding: "11px 12px 11px 15px"
  chip:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.text-muted}"
    rounded: "{rounded.md}"
    padding: "7px 14px"
---

# Design System: GitActionBoard

## Overview

**Creative North Star: "The Signal Board"**

GitActionBoard exists to answer one question at a glance: is anything red right now. Every visual decision serves that scan, and nothing else is allowed to compete with it. The system is calm and clinical by default — neutral card surfaces, one restrained accent hue, no redundant status badges, no whole-card color tinting — so that the single 4px left status-bar is the one and only place color carries meaning. When a build is in progress, that fact is communicated by an independent animated amber bar and nothing else; there is no "running…" label anywhere in the system, because if the UI has to say it in words, the visual signal has already failed.

This restraint was arrived at empirically, not by default minimalism: whole-card status tinting was built, computed against real WCAG contrast ratios, and reverted after it read as muddy and illegible at real desktop scale despite looking fine in small preview screenshots. A secondary border-color tint was tried and removed for the same reason — one signal, not two. The anti-reference is any dashboard that color-codes everything at once (tinted rows, badge pills, icon-and-text status duplicated three ways); that noise is precisely what this system refuses to do.

Density is a mode, not a redesign: "Personal" and "Build Monitor" are the same card, same tokens, same left-bar language — only grid density and type scale change between an at-your-desk view and an at-a-distance wallboard view.

**Key Characteristics:**

- One accent color (Signal Blue), used only for active/interactive state — never for status.
- Status lives only in the 4px left bar: green, coral, or grey. Never a badge, never a card tint.
- "Running" is a motion overlay (animated amber bar), independent of and layered on top of the last known status color.
- Two densities of one component, not two visual languages.
- Monospace (JetBrains Mono) is reserved for scannable data — repo names, run numbers, version — never for prose.

## Colors

The palette is almost entirely neutral; color is spent deliberately and only on two jobs: signaling interactive state (Signal Blue) and signaling build result (green / coral / grey / amber-for-motion).

### Primary

- **Signal Blue** (`#7c9eff` dark / `#4a63d6` light): The only accent color in the system. Marks active/selected state only — the active mode-pill segment, the active nav-rail item (paired with its 14%/8% soft tint as the item's background), and primary buttons. Never used to indicate build status.

### Secondary (status colors — not decorative, load-bearing)

- **Success Green** (`#4fb87a` dark / `#1f9d55` light): The status-bar color for a passing build. Its soft tint (14%/6% alpha) is reserved should a success-state background ever be needed, but is not used in the current mockup beyond the bar itself.
- **Error Coral** (`#e2685f` dark / `#d1483e` light): The status-bar color for a failing build. Deliberately a warm coral, not a saturated alarm red — the system stays calm even when reporting bad news.
- **Warning Amber** (`#d9a441` dark / `#b8790f` light): Reserved exclusively for the in-progress motion overlay (the sliding progress bar). Never appears as a status-bar color — amber does not mean "a build result," it means "something is happening right now."

### Neutral

- **Canvas** (`#14161a` dark / `#f6f7f9` light): The app shell background, one step behind every surface.
- **Surface** (`#1c1f26` dark / `#ffffff` light): Cards, topbar, nav rail, chips, the preferences card — the default resting surface.
- **Surface Raised** (`#23262e` dark / `#f0f1f4` light): A second, slightly-lifted neutral for elements that sit inside a surface (mode-pill track, avatar placeholder, meta-tag background, the "off" switch track).
- **Border** (`#2c303a` dark / `#e4e6ea` light): The only separator; hairline 1px, never a shadow-only division.
- **Text** (`#e7e9ee` dark / `#1c1f26` light): Primary text.
- **Text Muted** (`#9aa1af` dark / `#5b6472` light): Secondary text — meta rows, subtitles, chip labels. Verified numerically against the shipped implementation (sRGB→luminance formula): light theme 5.98:1 on Surface, 5.30:1 on Surface Raised; dark theme 6.35:1 on Surface, 5.83:1 on Surface Raised — all comfortably clear the AA 4.5 floor, but light theme's margin is the tighter of the two. Re-verify numerically after any token change here, don't eyeball it.

### Named Rules

**The One Signal Rule.** Build status is expressed in exactly one place: the left status-bar. If a change adds a second place status shows up (a badge, a tinted background, a colored border), that's a regression, not an enhancement.

**The Amber-Is-Motion Rule.** Amber/warning color never represents a build result. It exists solely on the animated progress overlay to mean "in progress," and always sits on top of — never instead of — the last known status color.

## Typography

**Display/Body Font:** Inter (with -apple-system, BlinkMacSystemFont, sans-serif fallback)
**Label/Mono Font:** JetBrains Mono, monospace

**Character:** A dense, functional UI face paired with a monospace face reserved for anything that is literally data (a repo slug, a run number, a version string) rather than prose — so the eye can tell "this is a value" from "this is a description" without reading either.

### Hierarchy

- **Title** (700, 20px, 1.2 line-height, -0.01em): Page-level heading only (e.g. "Workflow Jobs"). The largest text in the system — everything else is built from weight and color, not size.
- **Headline** (700, 15px): Card/panel headers, e.g. the preferences card title.
- **Body** (600, 13.5px, 1.3 line-height): Row labels, subtitles, primary UI copy — bold-leaning rather than large.
- **Label** (600, 10.5px, 1.3 line-height): Card meta rows, chip text, timestamps. Sans (Inter) when it's descriptive text (e.g. "3 hrs ago"), Mono (JetBrains Mono) when it's a literal identifier (repo name, `#1234` run tag, `v5.0.0`).

### Named Rules

**The Density-Not-Size Rule.** No UI text in this system exceeds 20px. Hierarchy is carried by font-weight (600/700) and color (text vs. text-muted), not by a large type scale — the system is built to be scanned in a grid, not read as prose.

## Layout

Two densities of one grid, never two layouts. Both use `grid-template-columns: repeat(auto-fill, minmax(Npx, 1fr))` with a fixed card height and a 2-line clamp on the title — cards never grow to fit content, content is truncated to fit the card.

- **Personal mode:** card min-width 260px, gap 12px, card height 112px. Tuned for roughly 6 columns at common desktop widths — enough breathing room to read a title comfortably.
- **Build Monitor mode:** card min-width 200px, gap 10px, card height 90px, status-bar widened to 5px, type scale bumped up (repo 12px, title 14px, meta 11.5px) to stay legible at wallboard viewing distance despite the smaller card.
- **Shell:** 64px topbar, 72px icon nav rail (44px square touch targets), content area padded 28px (vertical) / 32px (horizontal).
- **Preferences card:** a single-column, max-width 640px card with 24px horizontal / 16-20px vertical row padding — deliberately not a grid, since settings are read top-to-bottom, not scanned.

## Elevation & Depth

Flat by default; the only shadow in the system is one soft, ambient two-layer shadow (`0 1px 2px rgba(0,0,0,.4), 0 8px 24px -8px rgba(0,0,0,.5)` dark / `0 1px 2px rgba(20,22,26,.04), 0 8px 24px -12px rgba(20,22,26,.12)` light), applied identically to every card and the preferences panel. Its job is to separate a surface from the canvas behind it, not to imply interactivity or hierarchy between surfaces — there is no elevated/pressed/hover shadow state defined anywhere in the system today.

### Shadow Vocabulary

- **Ambient Rest** (`box-shadow: 0 1px 2px rgba(0,0,0,.4), 0 8px 24px -8px rgba(0,0,0,.5)` dark-theme value): The one shadow. Applied to every card and panel at rest; does not change on hover or focus in the current design.

### Named Rules

**The One Shadow Rule.** There is a single shadow in the entire system, applied uniformly. Don't introduce a second, deeper shadow for "important" cards — importance is the status-bar's job.

## Shapes

Two radius families: an 8px "functional" radius used almost everywhere (buttons, chips, cards, the mode-pill track, the brand mark), and a small set of exceptions — 4px on the tiny meta-tag, 10px on nav-rail icon tiles, 12px on the preferences card (the one larger, calmer surface), and full-round (50%/pill) reserved for anything that represents a person or a binary toggle (avatar, switch, switch knob). Borders are always a single 1px hairline in the border token; there is no double-border or inset-border treatment anywhere.

## Components

### Status Card (signature component)

The core repeating unit of the whole product — every other component exists to support this one. A neutral surface card with a 4px colored left status-bar (green/coral/grey) that is the sole status signal, plus an optional independent progress-bar overlay for in-progress builds.

- **Shape:** 8px corner radius, 1px border, ambient shadow.
- **Status bar:** absolutely positioned, full-height, 4px wide (5px in Build Monitor mode), colored by the last known real result only — success green, error coral, or grey (`#8a8f9c`) for unknown. Never removed or hidden even while a new run is in progress.
- **Progress overlay:** a separate 3px (4px in Build Monitor mode) bar pinned to the card's bottom edge, present only while `activity === Building`. A 40%-width amber highlight slides left-to-right on a 1.1s ease-in-out loop. It sits independently of the status bar — a card can correctly show "previously failed, running now" as a coral bar with an amber sliding highlight beneath it.
- **Content:** repo name (mono, muted, 10.5px), title (Inter, 12px/600, clamped to 2 lines in Personal mode), and a meta row (relative time + optional `#run` mono tag) pinned to the card's bottom via `margin-top: auto`.
- **Build Monitor title clamp:** 1 line, not 2. Verified against real (long) CI job titles at the 90px Build Monitor card height with a 14px title font: a 2-line clamp does not fit alongside the repo line and meta row and clips mid-glyph. A dense wall-display tile is scanned, not read closely, so a single truncated line is also the more correct affordance for that mode — this isn't just a size fix, it's the right call for the surface.
- **Never:** a status badge, a status word ("Passed"/"Running"), or a card-wide background/border tint. The status-bar and the progress overlay are the only vocabulary.

### Mode Pill

A two-segment switch (Personal / Build Monitor) that is a density toggle, not a page navigation.

- **Style:** track is Surface Raised at 8px radius with a 1px border and 3px inner padding; the active segment is a 6px-radius Signal Blue pill with white text; the inactive segment is transparent text-muted.

### Nav Rail Item

Vertical icon-over-label tile in the 72px-wide left rail.

- **Style:** 44px square, 10px radius, icon + 9px label stacked with 2px gap, text-muted by default; active state gets the Signal Blue soft-tint (14%/8% alpha) background with Signal Blue text/icon — the same soft-tint language used nowhere else, reserved for "this is where you are."

### Chips / Tags

- **Chip** (toolbar filter-style): Surface background, 1px border, 8px radius, 12.5px/600 muted text.
- **Meta-tag** (the `#1234` run-number tag inside a card): Surface Raised background, 4px radius, mono 9.5px — the smallest, quietest component in the system, present only when a run number exists.

### Switch (toggle)

- **Style:** 38×22px fully-rounded track. On: Signal Blue track, white knob right-aligned. Off: Surface Raised track with 1px border, text-muted knob left-aligned. No intermediate/indeterminate state exists.

### Buttons

- **Shape:** 8px radius, no border.
- **Primary:** Signal Blue background, white text, 600 weight, 10px/20px padding.
- **Hover/Focus:** not yet established in the mockup — this is a static reference, not a built surface. When implementing, keep the treatment consistent with the soft-tint language already used for active nav/pill states (e.g. a subtle darken or the `signal-blue-soft` tint on focus-visible) rather than introducing a new visual device.

## Do's and Don'ts

### Do:

- **Do** let the left status-bar be the only place a build's pass/fail/unknown result is expressed.
- **Do** treat "running" as an independent motion overlay layered on the last known status color, never as a 4th status or a text label.
- **Do** reuse the exact same card component across Personal and Build Monitor modes — vary only grid density and type scale, never the visual language.
- **Do** use JetBrains Mono only for literal identifiers (repo slugs, run numbers, version strings); everything else is Inter.
- **Do** recompute WCAG contrast numerically (sRGB→relative luminance→ratio) any time a text or muted-text color token changes, especially in the light theme, where the muted-text baseline is already close to the AA floor.

### Don't:

- **Don't** add a status badge, status word, or card-wide background/border tint — this was built, measured, and explicitly reverted this session for muddying legibility at real scale.
- **Don't** use warning amber to represent any build result — it means "in progress," nothing else.
- **Don't** invent a second visual language ("wall display" gradients, different card shape, etc.) for Build Monitor mode. It is a density preference (`enableBuildMonitorView`), not a different product.
- **Don't** manufacture explanatory copy ("first run", "no data yet") where the system currently just leaves the meta row empty — the UI communicates through color and motion, not sentences.
