package me.spoter.css

import scalacss.DevDefaults._

object GlobalStyle extends StyleSheet.Inline {

  import dsl._

  style(
    unsafeRoot("body")(
      margin.`0`,
      padding.`0`,
      fontSize(14.px),
      fontFamily :=! "Roboto, sans-serif"
    ),
    unsafeRoot(".container")(
      minHeight(700.px),
      margin(20.px)
    ),
    unsafeRoot(".ui-elem")(
      marginRight(10.px)
    ),
    unsafeRoot(".form-group")(
      marginRight(50.px)
    ),
    unsafeRoot(".action-icon")(
      fontSize(1.3.em),
      color.rgb(108, 117, 125),
      cursor.pointer,
    )
  )
}
