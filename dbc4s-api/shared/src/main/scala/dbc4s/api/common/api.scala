package dbc4s.api.common

package object api {
  case class ErrorResponse(error_code: String, message: String)
      extends Throwable
  object codec extends CodecCompat
  object NodeType {
    /**
     * Storage optimized
     * 
     * 30.5GB Memory, 4 Cores
    */
    val i3xlarge = "i3.xlarge"

    /**
     * Storage optimized
     * 
     * 61GB Memory, 8 Cores.
    */
    val i3x2large = "i3.x2large"

    /**
     * Memory optimized
     *
     * 30.5GB Memory, 4 Cores
     */
    val r4xlarge = "r4.xlarge"

  }
}
