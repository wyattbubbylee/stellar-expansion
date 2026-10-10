# Orbit stellar vista prototype

## What's implemented

- A stationary luminous sun scene 39 blocks north of the first station, positioned in the main window view.
- Irregular sphere made from glowing vanilla blocks and tinted glass.
- Thin glass corona rings and solar flare accents.
- Automatically created when the Space Access Beacon is used to enter Orbit, including worlds with an existing starter station.
- Safe-to-revisit persistence marker; no tick-driven entities or render loops.
- Avoids replacing nearby player structures by refusing to generate if the sun's envelope is occupied.

## Limitations

- Prototype is a physical block sculpture, **not** yet a custom sky renderer or animated plasma shader.
- The sky is still the vanilla End sky effect. Custom star fields and nebula fog are planned for a separate client visual update.
- No heat, solar energy generation, or physics yet.
- Test in a new disposable world before using the build for survival.

## Test checklist

1. Enter Orbit using `/give @s stellar_expansion:space_beacon`.
2. Look toward the station's north panoramic window (negative Z).
3. Confirm the sun renders and the station remains walkable.
4. Return to Overworld, then go to Orbit again: sun must not rebuild or replace edited station blocks.
5. Verify acceptable FPS on low render-distance settings.
