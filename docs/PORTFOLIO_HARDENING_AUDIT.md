# Portfolio hardening audit

## Executive summary

The original project compiled but Maven discovered no tests because it used non-standard source directories without a matching test configuration. The public tree also contained an internal PDF and 99 multimedia files with no provenance record. The hardening branch adopts the Maven layout, runs the model suite, fixes special-move/history defects, restricts Java deserialization and keeps the public UI usable through Unicode chess symbols.

## Runtime, structure and commands

- Language/runtime: Java 23.
- UI: JavaFX 23.0.1 (`javafx-controls` and `javafx-media`).
- Build: Maven; artifact `pt.isec.pa:chess:1.0.0`.
- Test framework: JUnit Jupiter 5.13.0 with Surefire 3.2.5.
- Executable entry point: `pt.isec.pa.chess.ui.MainJFX`, normally run with `mvn javafx:run`.
- Production sources: `src/main/java`; tests: `src/test/java`; optional local resources: `src/main/resources`.

Commands used:

```text
mvn clean test
mvn clean verify
mvn "-Dmaven.resources.skip=true" clean verify
```

The last form proves that compilation, tests and packaging do not depend on the removed local multimedia.

## Baseline evidence

- `mvn clean test` compiled successfully but reported `No tests to run`.
- `test/pt/.../GameBoardTest.java` contained 10 JUnit tests that Maven did not discover.
- `showOpenGameDialog` constructed an extension filter but never added it to the chooser.
- `ChessGameManager` saved a memento before every attempted move, including invalid moves.
- Loading `.dat` files used unrestricted `ObjectInputStream` and did not close streams deterministically.
- Castling did not reject a king in check or an attacked transit square.
- En passant depended on whether an adjacent pawn had moved once at any previous time, rather than on the immediately preceding two-square move.
- Promotion accepted non-standard piece types.
- A report PDF, 86 audio files and 13 images were tracked. Their README files contained no source or licence evidence; one image was institutional branding.
- Local `.idea`, `.iml`, `out` and `target` artefacts existed but were already ignored rather than tracked.
- No credential-shaped value was found in the tracked source scan.

## Risk classification

### P0

- None confirmed. No exposed credential or destructive data-loss path was found.

### P1

- Unrestricted Java deserialization of user-selected files.
- Invalid moves polluted undo history; history was also bound to the old game instance after loading a save.
- Castling and en passant accepted positions that violate chess rules.
- The documented test command silently executed zero tests.

### P2

- Non-standard Maven layout and an artificial `stage3` artifact version.
- Binary save compatibility is coupled to Java class serialization.
- Public assets had no provenance/licence evidence.
- README structure and test claims did not match the repository.
- JavaFX UI, accessibility and optional multimedia paths have no automated tests.

### P3

- Redundant direct JavaFX Graphics dependency and empty placeholder README files.
- The open-file extension filter was dead code.

## Corrections and verification

- Moved Java/resources/tests to the standard Maven directories and set version `1.0.0`.
- Removed the report, institutional image and unlicensed multimedia from the Git index while retaining ignored local copies.
- Added Unicode piece rendering and graceful no-audio behavior for clean public clones.
- Added the missing `.dat` `ExtensionFilter`.
- Added a bounded `ObjectInputFilter`, class allowlist, type check and try-with-resources to saved-game loading.
- Records a memento only after a valid move and rebinds the caretaker after loading.
- Enforces castling attack checks, a one-move en passant window and legal promotion choices.
- Added deterministic model/persistence tests and secret-free Windows/Linux Maven CI.

Final local result on 2026-08-01: `mvn clean verify` succeeded with 18 tests, 0 failures, 0 errors and 0 skipped. A second verify with Maven resources disabled also succeeded and produced `target/chess-1.0.0.jar`. CI configuration was added but its remote run was not observed in this audit.

## Remaining limitations

- Java serialization remains for backward compatibility; a versioned explicit format would further reduce risk.
- Only local Windows execution was observed. Linux compilation is configured in CI but not claimed until that run completes.
- Optional multimedia is intentionally absent from the public tree pending provenance and licence confirmation.
- No GUI, accessibility, threefold-repetition or fifty-move-rule tests exist.
- There is no authentication/authorization or network protocol because the application is an offline two-player desktop game.
- `SoundManager` owns a static JavaFX `MediaPlayer`; its lifecycle and JavaFX-thread behavior remain UI-level technical debt.
