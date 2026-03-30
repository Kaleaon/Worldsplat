package com.worldsplat

enum class UseCaseProfile(val label: String) {
    ARCHITECTURE("Architecture / AEC"),
    REAL_ESTATE("Real estate tours"),
    AR_PREVIEW("AR product preview"),
    CULTURAL_HERITAGE("Cultural heritage"),
    LARGE_OUTDOOR("Large outdoor mapping")
}

data class PipelinePlan(
    val capture: String,
    val preprocessRuntime: String,
    val training: String,
    val viewer: String,
    val notes: String
)

class PipelinePlanner {
    fun buildPlan(profile: UseCaseProfile): PipelinePlan {
        return when (profile) {
            UseCaseProfile.ARCHITECTURE -> PipelinePlan(
                capture = "Guided high-overlap capture + loop closures",
                preprocessRuntime = "LiteRT or ExecuTorch on NPU for depth/segmentation",
                training = "Cloud pipeline with planar constraints (PlanarGS/2DGS/SuGaR branch)",
                viewer = "Mobile splat viewer with section cuts + measurements",
                notes = "Prioritize flat-plane regularization and seam QA before export."
            )

            UseCaseProfile.REAL_ESTATE -> PipelinePlan(
                capture = "Fast walkthrough with exposure lock prompts",
                preprocessRuntime = "LiteRT default path for broad Android compatibility",
                training = "Fast profile in cloud with optional high-fidelity rerun",
                viewer = "Progressive loading viewer optimized for smooth tours",
                notes = "Optimize startup latency and model size over maximum geometric fidelity."
            )

            UseCaseProfile.AR_PREVIEW -> PipelinePlan(
                capture = "Tighter orbit shots around subject",
                preprocessRuntime = "ExecuTorch with vendor backend for low-latency features",
                training = "Hybrid: quick draft on device metadata + cloud refinement",
                viewer = "AR compositor with occlusion/depth-assisted placement",
                notes = "Latency budget dominates; keep assets aggressively compressed."
            )

            UseCaseProfile.CULTURAL_HERITAGE -> PipelinePlan(
                capture = "High-resolution multi-pass capture with metadata",
                preprocessRuntime = "ONNX Runtime (NNAPI/QNN) for reproducible model ops",
                training = "High-fidelity geometry profile with robust denoising",
                viewer = "Annotation-first viewer with archival export options",
                notes = "Prefer repeatability and provenance for preservation workflows."
            )

            UseCaseProfile.LARGE_OUTDOOR -> PipelinePlan(
                capture = "Segmented capture zones with geo-tagging",
                preprocessRuntime = "LiteRT/ONNX depending on device fleet consistency",
                training = "Hierarchical splat pipeline for scale-out",
                viewer = "Tile-streamed viewer with level-of-detail control",
                notes = "Design around memory, streaming, and battery constraints."
            )
        }
    }
}
