package codes.chrishorner.personasns

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.LongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animates snowflakes or cherry blossom petals - depending on the season.
 */
@Composable
fun BackgroundParticles(season: Season, modifier: Modifier = Modifier) {
  val state = remember { ParticlesState() }
  state.season = season

  if (season == Season.NONE) return

  val frameTimeMillis by rememberFrameTimeMillis()
  val particlePaths = rememberParticlePaths(season)

  Canvas(modifier = modifier.fillMaxSize()) {
    state.update(frameTimeMillis, worldSize = size.toDpSize())

    state.particles.fastForEach { particle ->
      val particlePath = particlePaths[particle.imageIndex]
      val x = particle.x.toPx()
      val y = particle.y.toPx()
      val center = Offset(x = particlePath.size.width / 2f, y = particlePath.size.height / 2f)

      withTransform(
        transformBlock = {
          translate(x, y)
          rotate(particle.rotation, pivot = center)
          scale(particle.scale, pivot = center)
        },
        drawBlock = {
          drawPath(particlePath.path, particle.color)
        },
      )
    }
  }
}

enum class Season {
  NONE,
  SPRING,
  WINTER,
}

private const val Sakura1 =
  "M60.17,9.57L68.37,0C73.59,4.93 80.2,13.69 80.2,24.1C80.2,33.38 73.47,48.63 60.97,56.65C49.15,48 43.46,34.5 43.46,25.79C43.46,17.08 45.88,7.52 54.43,0.65C55.41,2.54 60.17,9.57 60.17,9.57ZM112.47,43.75L124.08,48.76C121.26,55.36 115.68,63.44 105.9,67.04C97.2,70.25 80.47,69.36 68.64,60.4C72.66,46.31 83.45,36.15 91.63,33.14C99.81,30.13 110.6,30.22 119.99,35.87C118.56,37.45 112.47,43.75 112.47,43.75ZM96.22,105.19L95.78,117.78C88.62,117.18 77.96,113.41 71.46,105.26C65.68,98.02 62.69,82.23 67.46,68.17C82.1,67.55 93.74,75.27 99.17,82.08C104.61,88.89 108.67,97.88 106.27,108.58C104.33,107.72 96.22,105.19 96.22,105.19ZM31.53,107.94L20.44,113.1C19.67,106.08 19.51,95.93 25.26,87.25C30.39,79.52 43.26,69.51 58.1,69.74C63.18,83.49 61.64,98.91 56.82,106.17C52,113.43 43.33,119.27 32.42,120.27C32.64,118.15 31.53,107.94 31.53,107.94ZM11.99,47.53L3.49,38.62C9.74,35.09 19.78,32.67 29.73,35.74C38.59,38.47 51.46,48.09 55.44,62.4C43.69,71.15 28.84,73.55 20.52,70.98C12.19,68.42 4.04,62.33 0,52.14C2.09,51.76 11.99,47.53 11.99,47.53Z"
private const val Sakura2 =
  "M53.04,0L61.25,9.47C61.25,9.47 65.15,5 67.88,0.73C73.67,7.31 82.57,19.95 78.01,35.11C83.89,30.36 107.14,30.79 113.83,40.42C112.13,42.03 106.49,47.21 106.49,47.21L116.82,54.75C114.23,60.54 100.86,74.47 86.82,72.25C92.71,78.45 95.48,96.81 91.29,105.91C87.42,105.03 81.81,101.18 81.81,101.18C81.81,101.18 78.34,111.45 78.21,114.03C66.57,111.29 55.46,99.9 53.33,94.39C50.07,99.45 37.78,108.59 23.42,107.72C24.22,104.82 25.09,100.21 25.02,97.59C23.67,97.27 14.9,97.74 12.29,98.22C10.59,92.15 15.99,70.48 25.5,66.33C10.59,63.73 2.42,51.11 0,44.31C3.55,42.37 9.39,38.83 11.06,37.79C8.11,36.67 6.78,30.7 5.58,29.83C9.05,27.6 25.57,21.03 39.43,30C37.19,20.31 43.63,9.03 53.04,0Z"
private const val Sakura3 =
  "M53.03,8.441C51.03,5.89 39.85,0 31.86,0C18.14,0 13.58,7.785 0,15.482C5.73,18.082 14.36,28.846 32.84,28.846C41.62,28.846 50.28,21.565 53.51,19.206C50.74,17.528 46,13.403 46,13.403C47.53,11.753 50.78,9.484 53.03,8.441Z"
private const val Sakura4 =
  "M57.56,7.398C55.06,3.477 47.77,-0.269 37.05,0.015C11.23,0.698 10.17,21.993 0,27.329C4.92,28.344 21.78,33.506 34.5,33.072C44.04,32.747 53.31,27.889 59.41,19.397C57.11,17.87 51.87,11.328 51.87,11.328C51.87,11.328 56.16,8.412 57.56,7.398Z"

private const val Snowflake1 =
  "M13.93,41.31L1.66,34.19C0.07,33.26 -0.47,31.22 0.45,29.63C1.38,28.04 3.41,27.5 5.01,28.42L17.91,35.92L17.91,27.55C17.91,25.71 19.4,24.22 21.24,24.22C23.08,24.22 24.58,25.71 24.58,27.55L24.58,39.79L29.91,42.89L29.91,34.5C29.91,32.66 31.4,31.17 33.24,31.17C34.01,31.17 34.71,31.43 35.27,31.86C35.14,31.18 35.22,30.45 35.54,29.78C36.34,28.12 38.33,27.42 39.98,28.22L48.25,32.19L48.25,25.43C48.23,25.4 48.19,25.37 48.17,25.34L37.01,19.98C35.36,19.18 34.66,17.19 35.45,15.53C36.25,13.87 38.24,13.17 39.9,13.97L48.25,17.98L48.25,3.33C48.25,1.49 49.74,0 51.58,0C53.43,0 54.92,1.49 54.92,3.33L54.92,17.45L62.16,13.97C63.82,13.17 65.81,13.87 66.61,15.53C67.4,17.19 66.7,19.18 65.05,19.98L54.92,24.84L54.92,31.65L62.07,28.22C63.73,27.42 65.72,28.12 66.52,29.78C67.32,31.44 66.62,33.43 64.96,34.23L54.92,39.05L54.92,40.55L65.6,46.72L66.11,46.42L66.11,34.91C66.11,33.07 67.61,31.57 69.45,31.57C71.29,31.57 72.79,33.07 72.79,34.91L72.79,42.55L78.69,39.12L78.69,27.79C78.69,25.95 80.19,24.46 82.02,24.46C83.86,24.46 85.36,25.95 85.36,27.79L85.36,35.24L97.1,28.42C98.69,27.5 100.73,28.04 101.65,29.63C102.58,31.22 102.04,33.26 100.45,34.19L88.42,41.18L95.99,46.35C97.51,47.39 97.9,49.46 96.86,50.98C95.82,52.5 93.75,52.89 92.23,51.85L82.73,45.36C82.5,45.41 82.27,45.44 82.02,45.44C81.76,45.44 81.5,45.4 81.24,45.34L76.15,48.3L83.61,53.4C85.13,54.44 85.52,56.51 84.48,58.03C84.3,58.3 84.09,58.53 83.84,58.72C84.2,58.96 84.51,59.26 84.76,59.63C85.8,61.14 85.41,63.22 83.89,64.26L76.69,69.18L82.56,72.6L92.51,65.8C94.04,64.76 96.11,65.16 97.15,66.67C98.19,68.19 97.8,70.27 96.27,71.31L88.95,76.31L100.7,83.13C102.29,84.06 102.83,86.1 101.9,87.69C100.99,89.28 98.94,89.82 97.35,88.9L84.78,81.6L84.78,91.23C84.78,93.07 83.29,94.56 81.45,94.56C79.61,94.56 78.12,93.07 78.12,91.23L78.12,77.73L72.38,74.4L72.38,83.64C72.38,85.48 70.89,86.97 69.05,86.97C68.15,86.97 67.34,86.62 66.74,86.04C66.73,86.5 66.62,86.97 66.41,87.41C65.62,89.07 63.63,89.77 61.96,88.97L54.92,85.59L54.92,92.4L64.93,97.21C66.6,98.01 67.29,100 66.5,101.66C65.71,103.32 63.71,104.02 62.05,103.22L54.92,99.79L54.92,113.47C54.92,115.31 53.43,116.81 51.58,116.81C49.74,116.81 48.25,115.31 48.25,113.47L48.25,99.16L39.79,103.22C38.13,104.02 36.14,103.32 35.34,101.66C34.55,100 35.24,98.01 36.91,97.21L48.06,91.85C48.12,91.78 48.18,91.72 48.25,91.65L48.25,84.95L39.88,88.97C38.23,89.77 36.22,89.07 35.44,87.41C35.3,87.13 35.2,86.83 35.15,86.54C34.7,86.77 34.18,86.9 33.62,86.9C31.79,86.9 30.3,85.41 30.3,83.57L30.3,74.21L24.58,77.53L24.58,90.97C24.58,92.81 23.08,94.31 21.24,94.31C19.4,94.31 17.91,92.81 17.91,90.97L17.91,81.41L5.01,88.9C3.41,89.82 1.38,89.28 0.45,87.69C-0.47,86.1 0.07,84.06 1.66,83.13L13.48,76.27L6.97,71.82C5.45,70.78 5.06,68.71 6.09,67.19C7.13,65.67 9.21,65.28 10.73,66.32L19.86,72.56L25.75,69.14L19.35,64.77C17.83,63.73 17.44,61.66 18.48,60.14C18.74,59.75 19.08,59.43 19.45,59.2C18.91,58.96 18.42,58.56 18.06,58.03C17.02,56.51 17.41,54.44 18.93,53.4L26.19,48.44L20.44,45.1C20.38,45.08 20.32,45.07 20.27,45.05L10.3,51.85C8.79,52.89 6.72,52.5 5.68,50.98C4.64,49.46 5.03,47.39 6.54,46.35L13.93,41.31ZM36.51,33.87C36.55,34.07 36.57,34.29 36.57,34.5L36.57,46.76L36.88,46.94L48.25,40.38L48.25,39.77C48.15,39.67 48.05,39.56 47.97,39.45L37.1,34.23C36.89,34.13 36.69,34.01 36.51,33.87ZM68.88,52.52L68.88,64.65L70.29,65.47L79.88,58.93C79.87,58.92 79.86,58.91 79.84,58.9L70.36,52.42C70.07,52.51 69.76,52.55 69.45,52.55C69.27,52.55 69.09,52.54 68.91,52.51L68.88,52.52ZM65.71,70.58L54.92,76.82L54.92,78.19L64.85,82.96C65.18,83.12 65.46,83.32 65.71,83.56L65.71,70.58ZM48.25,76.99L36.96,70.48L36.96,82.98C36.97,82.97 36.99,82.97 37,82.96L47.86,77.74C47.98,77.59 48.11,77.45 48.25,77.32L48.25,76.99ZM33.99,64.36L33.99,52.96L32.58,52.15L22.69,58.9C22.65,58.93 22.62,58.95 22.58,58.97C22.77,59.05 22.95,59.15 23.11,59.27L32.13,65.43L33.99,64.36ZM51.43,46.24L40.65,52.46L40.65,64.91L51.43,71.13L62.21,64.91L62.21,52.46L51.43,46.24Z"
private const val Snowflake2 =
  "M47.75,101.72L47.75,93.55L43.83,97.65C42.94,98.58 41.46,98.61 40.53,97.73C39.6,96.84 39.57,95.36 40.45,94.43L47.75,86.79L47.75,79.28L41.63,84.04C40.62,84.83 39.15,84.65 38.36,83.63C37.57,82.61 37.75,81.15 38.77,80.36L46.99,73.96C47.07,73.73 47.18,73.5 47.32,73.3C47.45,73.12 47.59,72.98 47.75,72.85L47.75,72.22L38.19,66.7L37.44,67.14L36.68,78.41C36.6,79.7 35.48,80.67 34.2,80.59C32.91,80.5 31.94,79.39 32.02,78.1L32.57,70.02L27.39,73.07C27.39,73.1 27.38,73.13 27.37,73.15C27.13,74.08 26.34,74.72 25.46,74.85L23.91,84.35C23.71,85.62 22.5,86.48 21.23,86.28C19.96,86.07 19.1,84.87 19.31,83.6L20.33,77.24L13.44,81.31L15.82,85.32L0.2,86.41L8.7,73.27L11.07,77.29L18.19,73.09L13.05,71.7C11.8,71.36 11.07,70.08 11.4,68.84C11.74,67.6 13.02,66.86 14.26,67.19L23.8,69.77L29.86,66.19L24.36,64.3C23.15,63.88 22.5,62.55 22.92,61.34C23.34,60.12 24.67,59.47 25.88,59.89L35.14,63.08L36.24,62.43L36.24,52.81L34.48,51.77L24.41,56.25C23.24,56.78 21.85,56.25 21.34,55.07C20.81,53.89 21.34,52.51 22.52,51.99L29.56,48.85L22.36,44.57L14.05,47.57C12.84,48.01 11.51,47.38 11.07,46.17C10.63,44.96 11.26,43.62 12.47,43.18L17.17,41.48L10.84,37.72L8.46,41.73L-0,28.56L15.61,29.7L13.23,33.71L20.38,37.96L19.31,31.9C19.08,30.63 19.94,29.42 21.2,29.2C22.47,28.97 23.68,29.82 23.9,31.09L25.67,41.1L31.7,44.69L31.15,36.93C31.06,35.64 32.02,34.53 33.31,34.44C34.59,34.34 35.71,35.31 35.8,36.6L36.59,47.59L38.23,48.57L47.75,43.07L47.75,42.22C47.61,42.1 47.48,41.96 47.38,41.8C47.29,41.67 47.21,41.54 47.16,41.4L38.96,35.9C37.89,35.19 37.61,33.73 38.32,32.66C39.04,31.6 40.49,31.31 41.56,32.03L47.75,36.18L47.75,30.65C47.72,30.63 47.69,30.6 47.67,30.57C47.34,30.22 47.14,29.78 47.07,29.34L40.12,21.75C39.24,20.8 39.31,19.32 40.26,18.45C41.21,17.58 42.69,17.64 43.56,18.59L47.75,23.17L47.75,14L43.08,14L50.08,-0L57.08,14L52.42,14L52.42,22.92L57.17,18.47C58.11,17.59 59.59,17.64 60.47,18.57C61.35,19.51 61.3,20.99 60.36,21.87L52.42,29.32L52.42,35.5L57.45,32.04C58.51,31.31 59.96,31.58 60.69,32.64C61.42,33.7 61.15,35.16 60.09,35.89L52.42,41.17L52.42,43.21L61.4,48.4L63.52,47.17L63.84,38.11C63.89,36.82 64.97,35.81 66.26,35.86C67.55,35.91 68.56,36.99 68.51,38.28L68.28,44.4L75.08,40.46L76.38,31.35C76.57,30.07 77.75,29.19 79.03,29.37C80.3,29.56 81.19,30.74 81,32.01L80.22,37.48L86.94,33.58L84.6,29.54L100.22,28.57L91.62,41.65L89.28,37.62L82.87,41.34L87.89,42.83C89.12,43.19 89.82,44.49 89.46,45.73C89.09,46.96 87.79,47.67 86.56,47.3L77.32,44.55L70.16,48.71L76.89,51.45C78.08,51.93 78.66,53.3 78.17,54.49C77.69,55.68 76.32,56.26 75.13,55.77L65.05,51.67L63.69,52.47L63.69,62.66L65.02,63.43L74.4,59.51C75.59,59.01 76.95,59.57 77.45,60.76C77.95,61.95 77.38,63.31 76.2,63.81L70.11,66.36L75.73,69.6L86.72,67.17C87.98,66.89 89.22,67.69 89.5,68.94C89.77,70.2 88.98,71.45 87.72,71.73L81.72,73.06L89.26,77.4L91.59,73.36L100.22,86.41L84.6,85.49L86.93,81.44L80.24,77.59L81.01,83.68C81.17,84.96 80.27,86.13 78.99,86.29C77.71,86.45 76.54,85.54 76.38,84.27L75.17,74.67L74.78,74.44C74.18,74.36 73.64,74.04 73.27,73.57L68.02,70.55L68.02,78.43C68.02,79.72 66.98,80.77 65.69,80.77C64.4,80.77 63.36,79.72 63.36,78.43L63.36,67.86L61.54,66.81L52.42,72.08L52.42,74.11L60.95,80.31C61.99,81.07 62.22,82.53 61.47,83.57C60.71,84.61 59.25,84.84 58.21,84.08L52.42,79.88L52.42,87.14L59.93,94.36C60.86,95.25 60.89,96.73 59.99,97.65C59.1,98.58 57.62,98.61 56.7,97.72L52.42,93.61L52.42,101.72L57.08,101.72L50.08,115.72L43.08,101.72L47.75,101.72ZM50.02,68.07C50.77,67.64 59.02,62.87 59.02,62.87C59.02,62.87 59.02,52.41 59.02,52.41L49.96,47.18C49.96,47.18 40.9,52.41 40.9,52.41C40.9,52.41 40.9,62.87 40.9,62.87C40.9,62.87 49.21,67.67 49.91,68.08C49.95,68.08 49.98,68.07 50.02,68.07Z"
private const val Snowflake3 =
  "M14.352,34.098L5.032,39.607C3.442,40.544 1.402,40.018 0.462,38.434C-0.478,36.85 0.052,34.805 1.632,33.868L7.742,30.26L1.942,26.89C0.342,25.966 -0.198,23.924 0.732,22.333C1.652,20.743 3.692,20.201 5.282,21.125L11.292,24.611L11.392,17.268C11.422,15.429 12.942,13.957 14.772,13.984C16.612,14.011 18.092,15.526 18.062,17.366L17.902,28.451L25.402,32.81L25.402,24.602C25.402,23.769 25.852,22.998 26.572,22.581C27.292,22.165 28.182,22.165 28.902,22.581L36.092,26.733L36.092,17.128L27.382,12.401C25.772,11.524 25.172,9.499 26.042,7.882C26.922,6.265 28.952,5.665 30.562,6.542L36.092,9.543L36.092,3.333C36.092,1.493 37.592,-0.001 39.422,-0.001C41.272,-0.001 42.762,1.493 42.762,3.333L42.762,10.376L49.042,6.55C50.612,5.592 52.662,6.09 53.622,7.661C54.572,9.232 54.082,11.285 52.512,12.242L42.892,18.102C42.852,18.129 42.802,18.155 42.762,18.179L42.762,26.721L49.932,22.581C50.652,22.165 51.542,22.165 52.262,22.581C52.982,22.998 53.432,23.769 53.432,24.602L53.432,32.314L61.202,28.312L61.312,17.813C61.332,15.973 62.842,14.495 64.682,14.514C66.522,14.533 68.002,16.042 67.982,17.882L67.902,24.862L74.132,21.658C75.762,20.816 77.782,21.46 78.622,23.096C79.462,24.731 78.822,26.743 77.182,27.585L71.372,30.576L77.552,34.136C79.152,35.054 79.702,37.094 78.782,38.688C77.862,40.282 75.822,40.831 74.232,39.913L64.322,34.207L56.242,38.365L63.942,42.813C64.672,43.229 65.112,44 65.112,44.833C65.112,45.667 64.672,46.437 63.942,46.854L57.492,50.579L65.212,55.06L74.042,49.402C75.592,48.41 77.652,48.862 78.652,50.412C79.642,51.961 79.192,54.024 77.642,55.016L71.692,58.826L76.602,61.68C78.192,62.604 78.732,64.646 77.812,66.237C76.892,67.827 74.842,68.369 73.252,67.445L68.192,64.504L67.992,72.635C67.942,74.474 66.412,75.93 64.572,75.885C62.742,75.84 61.282,74.31 61.322,72.471L61.622,60.685L53.432,55.93L53.432,65.065C53.432,65.898 52.982,66.668 52.262,67.085C51.542,67.502 50.652,67.502 49.932,67.085L42.762,62.945L42.762,70.801L52.462,77.185C54.002,78.196 54.422,80.265 53.412,81.802C52.402,83.338 50.332,83.765 48.792,82.754L42.762,78.782L42.762,85.649C42.762,87.489 41.272,88.983 39.422,88.983C37.592,88.983 36.092,87.489 36.092,85.649L36.092,78.625L30.112,82.127C28.532,83.057 26.482,82.523 25.552,80.935C24.622,79.348 25.162,77.304 26.742,76.374L36.092,70.899L36.092,62.934L28.902,67.085C28.182,67.502 27.292,67.502 26.572,67.085C25.852,66.668 25.402,65.898 25.402,65.065L25.402,55.47L17.702,59.943L17.742,71.081C17.742,72.921 16.262,74.42 14.422,74.426C12.582,74.432 11.082,72.944 11.072,71.104L11.052,63.808L5.742,66.89C4.152,67.814 2.112,67.273 1.182,65.682C0.262,64.091 0.802,62.049 2.392,61.125L7.082,58.404L2.292,55.772C0.682,54.885 0.092,52.857 0.982,51.245C1.862,49.633 3.892,49.044 5.502,49.93L13.802,54.496L20.942,50.35L14.892,46.854C14.172,46.437 13.722,45.667 13.722,44.833C13.722,44 14.172,43.229 14.892,42.813L22.142,38.625L14.352,34.098ZM30.072,28.644L30.072,38.09C30.072,38.923 29.622,39.694 28.902,40.11C28.902,40.11 20.722,44.833 20.722,44.833L28.902,49.556C29.622,49.973 30.072,50.743 30.072,51.577C30.072,51.577 30.072,61.023 30.072,61.023L38.252,56.3C38.972,55.883 39.862,55.883 40.582,56.3C40.582,56.3 48.762,61.023 48.762,61.023L48.762,51.577C48.762,50.743 49.212,49.973 49.932,49.556C49.932,49.556 58.112,44.833 58.112,44.833L49.932,40.11C49.212,39.694 48.762,38.923 48.762,38.09C48.762,38.09 48.762,28.644 48.762,28.644L40.582,33.367C39.862,33.783 38.972,33.783 38.252,33.367L30.072,28.644ZM42.562,39.389L46.052,45.217L42.752,50.933L35.962,50.822L32.462,44.994L35.762,39.277L42.562,39.389Z"
private const val Snowflake4 =
  "M13.76,0C23.25,0 26.87,6.272 26.87,11.382C26.87,16.492 24.28,26.145 12.19,26.145C6.67,26.145 0,23.439 0,16.025C0,7.031 7.18,0 13.76,0Z"

private class ParticlePath(
  val path: Path,
  val size: Size,
)

private fun String.asParticlePath(density: Density): ParticlePath {
  val path = asPath(density)
  val bounds = path.getBounds()
  val size = Size(bounds.width, bounds.height)
  return ParticlePath(path, size)
}

@Composable private fun rememberParticlePaths(season: Season): List<ParticlePath> {
  val density = LocalDensity.current
  return remember(density, season) {
    when (season) {
      Season.NONE -> emptyList()
      Season.SPRING -> listOf(
        Sakura1.asParticlePath(density),
        Sakura2.asParticlePath(density),
        Sakura3.asParticlePath(density),
        Sakura4.asParticlePath(density),
      )

      Season.WINTER -> listOf(
        Snowflake1.asParticlePath(density),
        Snowflake2.asParticlePath(density),
        Snowflake3.asParticlePath(density),
        Snowflake4.asParticlePath(density),
      )
    }
  }
}

private data class Particle(
  var imageIndex: Int = 0,
  var color: Color = Color.Unspecified,
  var x: Dp = 0.dp,
  var y: Dp = 0.dp,
  var scale: Float = 0f,
  var rotation: Float = 0f,
  var xSpeed: Dp = 0.dp,
  var ySpeed: Dp = 0.dp,
  var rotationSpeed: Float = 0f,
  var period: Float = 0f,
  var amplitude: Float = 0f,
)

private const val START_PARTICLE_COUNT = 8
private const val MAX_PARTICLE_COUNT = 25
private const val MIN_SPAWN_INTERVAL_MS = 400L
private const val SPAWN_VARIANCE_MS = 600L
private val DESPAWN_BUFFER_SIZE = 100.dp

private class ParticlesState {
  private var initialized = false
  private val active = ArrayList<Particle>(MAX_PARTICLE_COUNT)
  private val pool = ArrayDeque<Particle>(MAX_PARTICLE_COUNT).apply {
    repeat(MAX_PARTICLE_COUNT) { add(Particle()) }
  }

  val particles: List<Particle>
    get() = active

  var season = Season.NONE
    set(value) {
      if (field != value) {
        field = value
        initialized = false
        pool.addAll(active)
        active.clear()
      }
    }

  private var nextSpawnTime = 0L
  private var lastUpdateTime = 0L

  fun update(time: Long, worldSize: DpSize) {
    if (!initialized) {
      repeat(START_PARTICLE_COUNT) { spawnParticle(worldSize, spawnInBounds = true) }
      initialized = true
    }

    if (time > nextSpawnTime && pool.isNotEmpty()) {
      nextSpawnTime = time + MIN_SPAWN_INTERVAL_MS + Random.nextLong(SPAWN_VARIANCE_MS)
      spawnParticle(worldSize)
    }

    val deltaSeconds = (time - lastUpdateTime) / 1000f

    for (index in active.indices.reversed()) {
      val particle = active[index]

      if (
        particle.x < -DESPAWN_BUFFER_SIZE ||
        particle.y > worldSize.height + DESPAWN_BUFFER_SIZE
      ) {
        active.remove(particle)
        pool.add(particle)
      } else {
        with(particle) {
          rotation += rotationSpeed * deltaSeconds
          x += xSpeed * deltaSeconds
          y += (period * sin(amplitude * x.value) + (ySpeed.value * deltaSeconds)).dp
        }
      }
    }

    lastUpdateTime = time
  }

  private fun spawnParticle(worldSize: DpSize, spawnInBounds: Boolean = false) {
    val particle = pool.removeFirstOrNull() ?: return

    particle.apply {
      imageIndex = Random.nextInt(4)
      color = when (season) {
        Season.NONE -> Color.Unspecified
        Season.SPRING -> if (Random.nextBoolean()) Color(0xFFF2657B) else Color(0xFFE7354A)
        Season.WINTER -> if (Random.nextBoolean()) Color(0xFF730D00) else Color(0xFF9F0B00)
      }
      x = when {
        spawnInBounds -> randomBetween(0.dp, worldSize.width)
        else -> randomBetween(0.dp, worldSize.width + 200.dp)
      }
      y = when {
        spawnInBounds -> randomBetween(0.dp, worldSize.height)
        else -> (-120).dp
      }
      scale = randomBetween(0.5f, 0.8f)
      rotation = randomBetween(0f, 360f)
      xSpeed = randomBetween(-80f, 6f).dp
      ySpeed = randomBetween(60f, 90f).dp
      rotationSpeed = randomBetween(22f, 28f) * if (Random.nextBoolean()) 1 else -1
      period = randomBetween(0.3f, 0.7f)
      amplitude = randomBetween(0.003f, 0.05f)
    }

    active.add(particle)
  }
}

/**
 * Uses [withFrameMillis] to update a state every frame.
 */
@Composable
private fun rememberFrameTimeMillis(): LongState {
  val millisState = remember { mutableLongStateOf(0L) }

  LaunchedEffect(Unit) {
    val startTime = withFrameMillis { it }

    while (true) {
      withFrameMillis { frameTime ->
        millisState.longValue = frameTime - startTime
      }
    }
  }

  return millisState
}