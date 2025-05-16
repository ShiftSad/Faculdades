package dev.shiftsad.movement.display

data class Pose(val normalizedLandmarks: List<Vec>, val worldLandmarks: List<Vec>) {
    companion object {
        const val LANDMARK_COUNT = 33
    }

    init {
        require(normalizedLandmarks.size == LANDMARK_COUNT)
        require(worldLandmarks.size == LANDMARK_COUNT)
    }
}

fun List<Vec>.nose() = this[0]
fun List<Vec>.leftEyeInner() = this[1]
fun List<Vec>.leftEye() = this[2]
fun List<Vec>.leftEyeOuter() = this[3]
fun List<Vec>.rightEyeInner() = this[4]
fun List<Vec>.rightEye() = this[5]
fun List<Vec>.rightEyeOuter() = this[6]
fun List<Vec>.leftEar() = this[7]
fun List<Vec>.rightEar() = this[8]
fun List<Vec>.mouthLeft() = this[9]
fun List<Vec>.mouthRight() = this[10]
fun List<Vec>.leftShoulder() = this[11]
fun List<Vec>.rightShoulder() = this[12]
fun List<Vec>.leftElbow() = this[13]
fun List<Vec>.rightElbow() = this[14]
fun List<Vec>.leftWrist() = this[15]
fun List<Vec>.rightWrist() = this[16]
fun List<Vec>.leftPinky() = this[17]
fun List<Vec>.rightPinky() = this[18]
fun List<Vec>.leftIndex() = this[19]
fun List<Vec>.rightIndex() = this[20]
fun List<Vec>.leftThumb() = this[21]
fun List<Vec>.rightThumb() = this[22]
fun List<Vec>.leftHip() = this[23]
fun List<Vec>.rightHip() = this[24]
fun List<Vec>.leftKnee() = this[25]
fun List<Vec>.rightKnee() = this[26]
fun List<Vec>.leftAnkle() = this[27]
fun List<Vec>.rightAnkle() = this[28]
fun List<Vec>.leftHeel() = this[29]
fun List<Vec>.rightHeel() = this[30]
fun List<Vec>.leftFootIndex() = this[31]
fun List<Vec>.rightFootIndex() = this[32]