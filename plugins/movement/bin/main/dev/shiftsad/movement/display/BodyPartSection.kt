package dev.shiftsad.movement.display

import org.bukkit.util.Vector

data class BodyPartSection(val bodyPartImageSource: BodyPartImageSource, val bodyPartTransform: BodyPartTransform) {
    companion object {
        val HEAD = BodyPartSection(
            BodyPartImageSource(
                front = Rect(8, 8, 8, 8),
                left = Rect(0, 8, 8, 8),
                back = Rect(24, 8, 8, 8),
                right = Rect(16, 8, 8, 8),
                bottom = Rect(16, 0, 8, 8),
                top = Rect(8, 0, 8, 8),
                outerLayerOffset = Vec2I(32, 0)
            ), BodyPartTransform(Vector(1.0, 1.0, 1.0), Vector(0.0, 0.25, 0.25))
        )
        val UPPER_BODY = BodyPartSection(
            BodyPartImageSource(
                front = Rect(20, 20, 8, 4),
                left = Rect(16, 20, 4, 4),
                back = Rect(32, 20, 8, 4),
                right = Rect(28, 20, 4, 4),
                bottom = null,
                top = Rect(20, 16, 8, 4),
                outerLayerOffset = Vec2I(0, 16)
            ), BodyPartTransform(Vector(1.0, 0.5, 0.5))
        )
        val LOWER_BODY = BodyPartSection(
            BodyPartImageSource(
                front = Rect(20, 24, 8, 8),
                left = Rect(16, 24, 4, 8),
                back = Rect(32, 24, 8, 8),
                right = Rect(28, 24, 4, 8),
                bottom = Rect(28, 16, 8, 4),
                top = null,
                outerLayerOffset = Vec2I(0, 16)
            ), BodyPartTransform(Vector(1.0, 1.0, 0.5), Vector(0.0, -0.25, 0.0))
        )
        val UPPER_LEFT_LEG = BodyPartSection(
            BodyPartImageSource(
                front = Rect(20, 52, 4, 4),
                left = Rect(16, 52, 4, 4),
                back = Rect(28, 52, 4, 4),
                right = Rect(24, 52, 4, 4),
                bottom = null,
                top = Rect(20, 48, 4, 4),
                outerLayerOffset = Vec2I(-16, 0)
            ), BodyPartTransform(Vector(0.5, 0.5, 0.5))
        )
        val LOWER_LEFT_LEG = BodyPartSection(
            BodyPartImageSource(
                front = Rect(20, 56, 4, 8),
                left = Rect(16, 56, 4, 8),
                back = Rect(28, 56, 4, 8),
                right = Rect(24, 56, 4, 8),
                bottom = Rect(24, 48, 4, 4),
                top = null,
                outerLayerOffset = Vec2I(-16, 0)
            ), BodyPartTransform(Vector(0.5, 1.0, 0.5), Vector(0.0, -0.25, 0.0))
        )
        val UPPER_RIGHT_LEG = BodyPartSection(
            BodyPartImageSource(
                front = Rect(4, 20, 4, 4),
                left = Rect(0, 20, 4, 4),
                back = Rect(12, 20, 4, 4),
                right = Rect(8, 20, 4, 4),
                bottom = null,
                top = Rect(4, 16, 4, 4),
                outerLayerOffset = Vec2I(0, 16)
            ), BodyPartTransform(Vector(0.5, 0.5, 0.5))
        )
        val LOWER_RIGHT_LEG = BodyPartSection(
            BodyPartImageSource(
                front = Rect(4, 24, 4, 8),
                left = Rect(0, 24, 4, 8),
                back = Rect(12, 24, 4, 8),
                right = Rect(8, 24, 4, 8),
                bottom = Rect(8, 16, 4, 4),
                top = null,
                outerLayerOffset = Vec2I(0, 16)
            ), BodyPartTransform(Vector(0.5, 1.0, 0.5), Vector(0.0, -0.25, 0.0))
        )
        val UPPER_LEFT_ARM: (slim: Boolean) -> BodyPartSection
        val LOWER_LEFT_ARM: (slim: Boolean) -> BodyPartSection
        val UPPER_RIGHT_ARM: (slim: Boolean) -> BodyPartSection
        val LOWER_RIGHT_ARM: (slim: Boolean) -> BodyPartSection

        init {
            val armWidthPx = { slim: Boolean -> if (slim) 3 else 4 }
            val armWidthScale = { slim: Boolean -> if (slim) 0.5 * 0.75 else 0.5 }
            UPPER_LEFT_ARM = { slim ->
                BodyPartSection(
                    BodyPartImageSource(
                        front = Rect(36, 52, armWidthPx(slim), 4),
                        left = Rect(32, 52, 4, 4),
                        back = Rect(if (slim) 43 else 44, 52, armWidthPx(slim), 4),
                        right = Rect(if (slim) 39 else 40, 52, 4, 4),
                        bottom = null,
                        top = Rect(36, 48, armWidthPx(slim), 4),
                        outerLayerOffset = Vec2I(16, 0)
                    ), BodyPartTransform(Vector(armWidthScale(slim), 0.5, 0.5))
                )
            }
            LOWER_LEFT_ARM = { slim ->
                BodyPartSection(
                    BodyPartImageSource(
                        front = Rect(36, 56, armWidthPx(slim), 8),
                        left = Rect(32, 56, 4, 8),
                        back = Rect(if (slim) 43 else 44, 56, armWidthPx(slim), 8),
                        right = Rect(if (slim) 39 else 40, 56, 4, 8),
                        bottom = Rect(if (slim) 39 else 40, 48, armWidthPx(slim), 4),
                        top = null,
                        outerLayerOffset = Vec2I(16, 0)
                    ), BodyPartTransform(Vector(armWidthScale(slim), 1.0, 0.5), Vector(0.0, -0.25, 0.0))
                )
            }
            UPPER_RIGHT_ARM = { slim ->
                BodyPartSection(
                    BodyPartImageSource(
                        front = Rect(44, 20, armWidthPx(slim), 4),
                        left = Rect(40, 20, 4, 4),
                        back = Rect(if (slim) 51 else 52, 20, armWidthPx(slim), 4),
                        right = Rect(if (slim) 47 else 48, 20, 4, 4),
                        bottom = null,
                        top = Rect(44, 16, armWidthPx(slim), 4),
                        outerLayerOffset = Vec2I(0, 16)
                    ), BodyPartTransform(Vector(armWidthScale(slim), 0.5, 0.5))
                )
            }
            LOWER_RIGHT_ARM = { slim ->
                BodyPartSection(
                    BodyPartImageSource(
                        front = Rect(44, 24, armWidthPx(slim), 8),
                        left = Rect(40, 24, 4, 8),
                        back = Rect(if (slim) 51 else 52, 24, armWidthPx(slim), 8),
                        right = Rect(if (slim) 47 else 48, 24, 4, 8),
                        bottom = Rect(if (slim) 47 else 48, 16, armWidthPx(slim), 4),
                        top = null,
                        outerLayerOffset = Vec2I(0, 16)
                    ), BodyPartTransform(Vector(armWidthScale(slim), 1.0, 0.5), Vector(0.0, -0.25, 0.0))
                )
            }
        }
    }
}