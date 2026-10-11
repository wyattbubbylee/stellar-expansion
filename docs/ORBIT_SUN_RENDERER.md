# Orbit cinematic red giant — first render implementation

## Design target

A massive red/orange giant with white-hot center, broad warm glow and subtle animated solar corona. This is **client-side sky rendering**, not blocks, entities, or particles. The north-facing observation gallery is retained.

## Files

- `client/render/OrbitSceneRenderer.java`: subscribes to NeoForge `RenderLevelStageEvent.Stage.AFTER_SKY`; checks `SpaceBeaconItem.ORBIT` before any rendering; uses fixed-distance camera-relative vertex gradients and animated corona rings.
- `client/OrbitStarVfx.java`: deleted (previous short-range particle swarm).
- `.github/workflows/build.yml`: artifact includes run number and commit SHA, avoiding identical-looking downloads.

## Current effect and limits

- Radial colored vertex gradients emulate broad bloom without shader post-processing.
- The corona oscillates slowly; no CPU-side particle spawning or chunk generation.
- Particle settings act as a temporary graphics-quality selector: minimal = gradient only, decreased = one corona sheet, all = two.
- The renderer uses the vanilla position-color shader, with depth writes disabled for the sky pass.
- This initial stage does **not** yet provide real HDR bloom, textured solar surface, a custom nebula/starfield, or user-facing settings; those are follow-ups after the first actual in-game screenshot and performance baseline.
- The current Orbit dimension uses `minecraft:the_end` special effects. Verify its native sky drawing does not obscure or conflict with the new sun before changing the dimension resource.

## Test checklist

1. Confirm CI Java 21 + NeoForge build passes. A passing build is **not** proof that the effect looks correct.
2. Start a clean disposable world on Minecraft 1.21.1 / NeoForge, extract the JAR from the artifact ZIP and remove older copies in Prism.
3. Run `/give @s stellar_expansion:space_beacon` and right-click the beacon.
4. Walk to the north-facing glass observation gallery and look north at the giant.
5. Verify geometry is behind glass/hull (not screen-space overlay), follows sky direction while walking, and stays visible regardless of render distance and particle setting.
6. Check image size and whether sun dominates the window view; capture screenshots from inside the gallery.
7. Look in other dimensions for accidental rendering, then return to Orbit and check world stability.
8. Compare FPS at particles All, Decreased, and Minimal; check GPU compatibility on Radeon RX 580.
9. If the disc is absent, clipped, behind End sky, or drawn over nearby blocks, revisit matrix handling and dimension special effects / sky stage before merging.

## Next rendering pass

Add custom smooth plasma/corona textures, starfield, optional shader-style bloom and a proper graphics config. Keep the effect bounded and entirely client-side. After the sun is verified in-game, move on to the physical holographic orbital map table.
