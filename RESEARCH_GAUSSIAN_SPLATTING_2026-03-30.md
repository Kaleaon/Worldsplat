# Gaussian Splatting Research Brief (as of 2026-03-30)

## 1) Core foundation and major breakthroughs

## Canonical baseline
- **3D Gaussian Splatting (3DGS, SIGGRAPH 2023)** established the practical baseline for real-time radiance-field rendering from calibrated photos/video.
  - Project: https://repo-sam.inria.fr/fungraph/3d-gaussian-splatting/
  - Code: https://github.com/graphdeco-inria/gaussian-splatting

## High-impact follow-up directions (2024-2026)
1. **Geometry accuracy / surface quality**
   - **2D Gaussian Splatting (2DGS, 2024)** replaces volumetric 3D ellipsoids with oriented planar disks, improving geometric consistency and mesh extraction quality.
     - https://arxiv.org/abs/2403.17888
   - **SuGaR (2023/2024)** introduces surface alignment regularization for fast, precise mesh extraction from splats.
     - https://arxiv.org/abs/2311.12775
   - **GaussianRoom (2024)** couples splats with SDF guidance and monocular priors; strong for indoor/architectural texture-poor scenes.
     - https://arxiv.org/abs/2405.19671
   - **PlanarGS (2025)** adds planar priors, flattening, and co-planarity constraints for indoor scene fidelity.
     - https://planargs.github.io/
     - paper page: https://planargs.github.io/static/planargs.pdf

2. **Anti-aliasing and stability across zoom/scales**
   - **Mip-Splatting (2023)** introduces anti-aliasing improvements (3D smoothing + mip-style filtering), reducing scale-dependent artifacts.
     - https://arxiv.org/abs/2311.16493
   - **SA-GS (2024)** test-time scale-adaptive anti-aliasing plugin style improvements.
     - https://arxiv.org/abs/2403.19615

3. **Training robustness / optimization reformulation**
   - **3DGS as MCMC (2024)** reframes densification/pruning as sampling transitions, reducing heuristic fragility.
     - https://arxiv.org/abs/2404.09591

4. **Large scenes and memory efficiency**
   - **Hierarchical 3D Gaussians (SIGGRAPH 2024)** improves very-large-scene scalability through hierarchy/chunking.
     - https://repo-sam.inria.fr/fungraph/hierarchical-3d-gaussians/
     - https://github.com/graphdeco-inria/hierarchical-3d-gaussians
   - **Reduced/Compressed 3DGS families (2024+)** reduce storage and runtime footprint for deployment.
     - https://github.com/graphdeco-inria/reduced-3dgs

---

## 2) Best tooling stack for **architecture** workflows

For architecture/AEC use-cases, “best” means: reliable camera solve, straight planar geometry, seam control, export/interoperability, and predictable iteration.

## Recommended production stack (open + practical)

1. **Capture + camera solving**
   - Start with controlled photo/video capture and robust SfM poses.
   - Use **COLMAP** compatible pipelines (3DGS and nerfstudio ecosystems depend on this heavily).

2. **Primary training framework**
   - **nerfstudio + gsplat/splatfacto** for modular experimentation and reproducibility.
     - Docs: https://docs.nerf.studio/
     - Method notes: https://docs.nerf.studio/nerfology/methods/nerf2gs2nerf.html

3. **Geometry-centric branch for architecture**
   - If you need crisp walls/floors/ceilings and CAD handoff, test:
     - **2DGS** or **SuGaR** (better surface/mesh behavior)
     - **PlanarGS / GaussianRoom** style methods for planar priors and weak-texture robustness.

4. **Large-site scalability**
   - For campus/building-scale scenes, evaluate **Hierarchical 3D Gaussians**.

5. **Commercial convenience tooling**
   - **Polycam** supports Gaussian Splatting workflows (notably device support differs by platform).
     - Device support notes: https://learn.poly.cam/hc/en-us/articles/34419168797972-Which-Devices-Are-Supported-by-Polycam
   - Use for rapid scan iteration; keep open-source path for advanced geometry constraints and custom export.

## Architecture-specific “flat planes + seams” playbook

1. **Data capture discipline (critical)**
   - Constant exposure/white balance, high overlap, varied baselines, avoid motion blur.
   - For long corridors or repeating texture, add oblique shots and loop closures.

2. **Planar priors in optimization**
   - Use methods with explicit planar constraints (PlanarGS-like) or SDF-coupled constraints (GaussianRoom-like).
   - For classic 3DGS pipelines, inject plane constraints post-SfM via segmentation or Manhattan-world assumptions.

3. **Regularize geometry before appearance overfitting**
   - Prefer schedules that strengthen normal/depth consistency early.
   - Use anti-aliasing variants (Mip-Splatting/SA-GS) to reduce edge shimmer that reads as seam artifacts.

4. **Seam management in practice**
   - Capture with radiometric consistency.
   - Use exposure compensation where available (the reference 3DGS repo notes exposure/AA feature updates in Oct 2024).
   - For deliverables, bake a mesh/texture branch (SuGaR/2DGS style) for clean CAD/BIM adjacency checks.

---

## 3) Android app path for native NPU/TPU acceleration

Your goal has two separable workloads:
1) **3D reconstruction/training** (heavy)
2) **real-time splat rendering + lightweight refinement** (mobile feasible)

## Practical recommendation
- Do **full splat training off-device** (desktop/cloud) for quality and thermal reasons.
- On Android, do:
  - On-device **inference components** (depth/segmentation/pose/refinement)
  - Real-time **splat viewer/renderer**
  - Optional short local optimization windows only.

## Android acceleration options (2026 snapshot)

1. **Google AI Edge / LiteRT**
   - LiteRT focuses on CPU/GPU/NPU acceleration and cross-platform deployment.
   - NPU path docs:
     - https://ai.google.dev/edge
     - https://ai.google.dev/edge/litert/next/acceleration
     - https://ai.google.dev/edge/litert/next/npu

2. **ONNX Runtime Mobile**
   - Android deployment with NNAPI/QNN execution providers.
   - Good if your model pipeline is ONNX-centric.
   - Docs:
     - https://onnxruntime.ai/docs/build/android.html
     - https://onnxruntime.ai/docs/execution-providers/NNAPI-ExecutionProvider.html
     - https://onnxruntime.ai/docs/execution-providers/QNN-ExecutionProvider.html

3. **ExecuTorch (PyTorch edge runtime)**
   - Android AAR integration + vendor backends (Qualcomm, MediaTek, Arm routes documented).
   - Useful if training/export pipeline is PyTorch-native.
   - Docs:
     - https://pytorch.org/mobile/android
     - https://docs.pytorch.org/executorch/stable/backends-overview.html
     - https://docs.pytorch.org/executorch/1.1/android-mediatek.html

4. **Android NNAPI (platform API)**
   - Official Android docs still describe NNAPI as the low-level API intended to be used via ML runtimes/frameworks.
   - https://developer.android.com/ndk/guides/neuralnetworks/

## “TPU on Android” clarification
- For most Android developers, practical acceleration is via **NPU/DSP/GPU paths** exposed by LiteRT, NNAPI, QNN, or vendor SDKs.
- “TPU” terminology on phones is often device/vendor-specific marketing; integration generally still happens through one of the runtimes above.

---

## 4) Suggested end-to-end product architecture (for your app)

1. **Capture module (Android)**
   - Guided capture UX: overlap meter, blur warning, exposure lock hints, loop-closure prompts.

2. **Local preprocessing (Android NPU/GPU accelerated)**
   - Depth estimation, semantic masks, feature extraction.
   - Export a robust capture package (frames + intrinsics + metadata).

3. **Training service (cloud/desktop)**
   - Run multi-profile pipeline:
     - fast preview profile (minutes)
     - architecture profile with planar constraints + geometry regularization (longer)

4. **Postprocess for architecture**
   - Plane extraction, seam QC, mesh extraction, texture baking, unit-scale checks.

5. **Mobile viewer/editor**
   - Stream/load splats, clipping planes, section cuts, measurement overlays.
   - Optional on-device refinement passes for small local edits.

---

## 5) Decision matrix (quick)

- If priority is **best geometric planes/seams**: start from **PlanarGS / GaussianRoom / 2DGS / SuGaR** ideas.
- If priority is **mature engineering ecosystem**: start from **nerfstudio + gsplat/splatfacto** and add planar constraints.
- If priority is **mobile runtime portability**: choose one runtime lane early:
  - **PyTorch lane** → ExecuTorch
  - **ONNX lane** → ONNX Runtime (NNAPI/QNN)
  - **Google edge lane** → LiteRT

