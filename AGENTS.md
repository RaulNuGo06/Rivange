## Working mode

- Use **Caveman (full mode)** by default unless explicitly told otherwise.
- Follow **YAGNI strictly**.
- Always choose the **simplest solution that works**.
- Prefer **direct implementations over abstractions** by default.
- Suggest a better abstraction or pattern only when it clearly improves readability, reuse, or separation of concerns.
- Do not add anything that was not explicitly requested.

---

## Core principles

- Keep solutions **small, local, and understandable**.
- Prefer **readability over cleverness**.
- Prefer **explicit code over implicit behavior**.
- Prefer **flat code over deeply nested code**.
- Prefer **cohesion over reuse** until reuse is real.
- Avoid over-engineering at all costs.

---

## Scope control

- Make the **minimum required changes** to solve the task.
- Do **not refactor unrelated code**.
- Keep changes **contained** unless a small restructure clearly improves the current task.
- Refactor only when it directly helps the requested change.
- Do not add “just in case” code.
- Do not implement future-proofing that was not asked for.

---

## Architecture

Use structure only when it improves clarity.

Typical valid separations:

- Entities / Models
- Services
- Controllers
- DTOs
- Mappers
- Frontend Components

Rules:

- Prefer **feature-based grouping** over generic shared folders.
- Create a new file, class, or module only when it improves cohesion or readability.
- Do not split logic across files if that makes the change harder to follow.
- Avoid generic helpers with no proven reuse.

When introducing an abstraction:

- Keep it **minimal**
- Explain **why it helps**
- Make sure it improves the current task, not a hypothetical future one

---

## Dependencies

- Do **not add new production dependencies** without explicit confirmation.
- Prefer:
 1. Native platform features
 2. Existing project dependencies
 3. A new dependency only if truly necessary

If a new dependency seems useful:

- Explain why it is needed
- Mention the simplest alternative, if one exists

---

## Data handling

- Assume data is already **normalized and valid** unless told otherwise.
- Do **not** add defensive transformations, fallback mapping, or normalization logic unless required.
- Do not add validation “just in case”.
- Do not reshape existing data contracts unless the task requires it.

---

## Implementation rules

When implementing:

- Solve only the requested problem
- Keep the logic as direct as possible
- Avoid unnecessary indirection
- Avoid premature extraction of utilities
- Avoid reusable abstractions unless reuse already exists or is immediate and obvious

Good default approach:

1. Understand the exact requirement
2. Change the smallest possible surface
3. Keep the implementation explicit
4. Stop once the requirement is fulfilled

---

## Code style

Prefer:

- Clear names
- Small functions
- Straightforward control flow
- Local reasoning
- Simple data flow

Avoid:

- Clever one-liners that hurt readability
- Deep abstraction layers
- Generic utility dumping
- Premature DRY
- Hidden side effects

---

## Security awareness

Evaluate security only when it is relevant to the task.

Focus on real risks such as:

- Input injection
- Authentication / authorization mistakes
- Sensitive data exposure
- Unsafe file handling
- Unsafe external requests
- Payment or permission-sensitive flows

Rules:

- Do not over-secure by default
- Do not add heavy security layers unless needed
- If you detect a meaningful risk, explain it briefly and propose the smallest reasonable fix

---

## Build and execution

- Do **not** run heavy or unnecessary commands unless explicitly requested.
- **Never run `npm run dist`** after modifying TypeScript files.
- Avoid unnecessary builds, installs, migrations, or formatting passes.
- Only run what is needed to validate the requested change.

---

## Output expectations for Codex

When responding with code changes:

- Keep the solution minimal
- Preserve existing patterns unless they clearly hurt the task
- Mention any abstraction introduced and why it is justified
- Call out relevant security concerns only if they materially apply
- Do not propose extra refactors outside the request
- Do not invent requirements

When multiple solutions are possible:

- Choose the simplest one
- Prefer the one with the lowest change surface
- Prefer the one easiest to maintain locally

---

## Final check before finishing

Before completing a task, verify:

- No unnecessary code was added
- No unrelated code was changed
- No hidden complexity was introduced
- No assumptions beyond the prompt were made
- The result is the **smallest working solution**
- Any abstraction introduced is **minimal and justified**