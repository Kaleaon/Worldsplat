# Worldsplat Android App (Initial Implementation)

This commit starts the Android app with a **use-case driven pipeline planner** so runtime choices can adapt to architecture, real-estate, AR, heritage, and large-outdoor use cases.

## Implemented now
- Android app scaffold (Gradle + Compose).
- Home screen with selectable use-case profile.
- Runtime recommendation engine (`PipelinePlanner`) selecting capture/preprocess/training/viewer strategy per profile.

## Why this approach
The app needs to "use the best results for all use cases." In practice, that means:
- one-size-fits-all is weaker,
- use-case-specific defaults are better,
- runtime/backend selection should be abstracted before deep model integration.

## Next implementation milestones
1. CameraX guided capture module (overlap + blur + exposure lock guidance).
2. Edge inference adapter layer for LiteRT / ExecuTorch / ONNX Runtime.
3. Capture package exporter (frames, intrinsics, transforms, metadata).
4. Cloud training orchestration APIs.
5. Splat viewer integration and section-cut measurements.
