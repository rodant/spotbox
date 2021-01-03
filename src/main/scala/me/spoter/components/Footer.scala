package me.spoter.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^.{^, _}

object Footer {

  private val component = ScalaComponent.builder
    .static("Footer")(
      <.footer(^.id := "site-footer", ^.className := "site-footer", ^.role := "contentinfo",
        <.div(^.className := "max-width",
          <.aside(^.id := "sidebar-footer-area", ^.className := "sidebar widget-area sidebar-footer-area active-3", ^.role := "complementary",
            <.section(^.id := "nav_menu-3", ^.className := "widget widget_nav_menu", <.div(^.className := "menu-fusszeilenmenu-container",
              <.ul(^.id := "menu-fusszeilenmenu", ^.className := "menu",
                <.li(^.id := "menu-item-561", ^.className := "menu-item menu-item-type-post_type menu-item-object-page menu-item-home menu-item-561",
                  <.a(^.href := "https://spoter.me/", ^.target:= "_blank", "For App Providers")),
                <.li(^.id := "menu-item-201", ^.className := "menu-item menu-item-type-post_type menu-item-object-page menu-item-201",
                  <.a(^.href := "https://spoter.me/spot", ^.target:= "_blank", "For Users")),
                <.li(^.id := "menu-item-534", ^.className := "menu-item menu-item-type-post_type menu-item-object-page menu-item-534",
                  <.a(^.href := "https://spoter.me/about-us", ^.target:= "_blank", "About us")),
                <.li(^.id := "menu-item-752", ^.className := "menu-item menu-item-type-post_type menu-item-object-page menu-item-752",
                  <.a(^.href := "https://spoter.me/blog", ^.target:= "_blank", "Blog")),
                <.li(^.id := "menu-item-80", ^.className := "menu-item menu-item-type-post_type menu-item-object-page menu-item-80",
                  <.a(^.href := "https://spoter.me/kontaktaufnahme", ^.target:= "_blank", "Contact")),
                <.li(^.id := "menu-item-33", ^.className := "menu-item menu-item-type-post_type menu-item-object-page menu-item-33",
                  <.a(^.href := "https://spoter.me/impressum", ^.target:= "_blank", "Impressum")),
                <.li(^.id := "menu-item-67", ^.className := "menu-item menu-item-type-post_type menu-item-object-page menu-item-67",
                  <.a(^.href := "https://spoter.me/haftungsausschluss", ^.target:= "_blank", "Haftungsausschluss")),
                <.li(^.id := "menu-item-642", ^.className := "menu-item menu-item-type-post_type menu-item-object-page current-menu-item page_item page-item-638 current_page_item menu-item-642",
                  <.a(^.href := "https://spoter.me/datenschutzerklaerung-privacy-policy", ^.target:= "_blank", "Datenschutzerklärung / Privacy Policy")),
              ))),
            <.section(^.id := "nav_menu-5", ^.className := "widget widget_nav_menu",
              <.div(^.className := "menu-secondary-container",
                <.ul(^.id := "menu-secondary", ^.className := "menu",
                  <.li(^.id := "menu-item-221", ^.className := "menu-item menu-item-type-custom menu-item-object-custom menu-item-221",
                    <.a(^.href := "https://solidproject.org/", ^.target:= "_blank", "Solid")),
                  <.li(^.id := "menu-item-323", ^.className := "menu-item menu-item-type-custom menu-item-object-custom menu-item-323",
                    <.a(^.href := "https://github.com/spoter-ME", ^.target:= "_blank", "Github")),
                ))),
            <.section(^.id := "media_image-5", ^.className := "widget widget_media_image",
              <.img(^.width := 150.px, ^.maxWidth := "100%", ^.height := 150.px,
                ^.src := "https://spoter.me/wp-content/uploads/2020/05/INVEST_Logo_URL_rote_URL_RZ-150x150.png",
                ^.className := "image wp-image-181  attachment-thumbnail size-thumbnail lazy-loaded")),
          ),
          <.div(^.className := "site-credit",
            <.a(^.href := "https://spoter.me", ^.target:= "_blank", "spoter.ME"), ^.target:= "_blank", "Digitalization with the focus on people")
        ))).build

  def apply(): Unmounted[Unit, Unit, Unit] = component()
}
