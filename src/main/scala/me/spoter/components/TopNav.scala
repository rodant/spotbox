package me.spoter.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import me.spoter.components.bootstrap._
import me.spoter.components.solid.Value
import me.spoter.models.User
import me.spoter.models.rdf.IRI
import me.spoter.services._
import me.spoter.{Session, SessionTracker, StateXSession}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class State(user: User, dialogShown: Boolean = false, podName: String = "", currentErrMessage: Option[String] = None)

class Backend(bs: BackendScope[Unit, StateXSession[State]]) {
  def render(stateXSession: StateXSession[State]): VdomElement = {
    val loggedIn = stateXSession.session.isDefined
    NavBar(expand = "lg", bg = "dark", variant = "dark")(
      NavBarBrand("#")(
        <.div(^.display := "flex",
          <.img(^.src := "public/spotbox/images/logo.png", ^.alt := "spoter.ME",
            ^.className := "d-inline-block align-top", ^.width := 205.px, ^.height := 35.px))),
      NavBarToggle()(^.aria.controls := "basic-navbar-nav"),
      NavBarCollapse()(^.id := "basic-navbar-nav")(
        Nav()(^.className := "mr-auto")(
          renderWhen(loggedIn)(<.div(
            renderWhen(ResourceService.getSpotPodFromStore(stateXSession.state.user).nonEmpty)(
              Button(variant = "secondary", onClick = startPodCreation(_))("Delete spoter.ME POD"))
              .getOrElse(Button(onClick = startPodCreation(_))("Create spoter.ME POD")),
            renderDialog(stateXSession))
          )
        ),
        Nav()(^.id := "login-button",
          NavBarText()(^.id := "logged-in-user", ^.className := "ui-elem", Value("user.name")).when(loggedIn),
          <.div(^.id := "login-button", AuthButton("https://solidcommunity.net/common/popup.html", loggedIn = loggedIn)))),
      renderError(stateXSession)
    )
  }

  private def renderDialog(stateXSession: StateXSession[State]): VdomElement = {
    val (user, dialogShown, podName) = (stateXSession.state.user, stateXSession.state.dialogShown, stateXSession.state.podName)
    val hasSpotPod = ResourceService.getSpotPodFromStore(user).nonEmpty
    Modal(size = "lg", show = dialogShown, onHide = (_: Unit) => setDialogShownFlag(false))(
      ModalHeader(closeButton = true)(
        ModalTitle()(
          if (hasSpotPod) "Delete your POD from spoter.ME Server"
          else "Create own POD on spoter.ME Server")
      ),
      ModalBody()(
        if (hasSpotPod)
          <.p("You are about to delete your POD from the spoter.ME server. The POD must be empty, otherwise the operation will fail.")
        else {
          <.div(
            <.p("You are about to create an own Solid POD on the spoter.ME server. Please fill out the next form and confirm."),
            Form(validated = true)(^.noValidate := true, ^.onSubmit ==> dismissOnSubmit)(
              FormControl(value = podName, size = "sm", onChange = onChangeName(_))(
                ^.placeholder := "POD Name", ^.autoFocus := true, ^.required := true, ^.minLength := 3, ^.maxLength := 40,
                ^.onKeyUp ==> handleKey)()
            )
          )
        }
      ),
      ModalFooter()(
        Button(variant = "secondary", onClick = (_: ReactEventFromInput) => setDialogShownFlag(false))("Cancel"),
        if (hasSpotPod) Button(onClick = confirmPodDeletion(user)(_))("Delete POD")
        else Button(onClick = confirmPodCreation(user, podName)(_))("Create POD")
      )
    )
  }

  private def renderError(sxs: StateXSession[State]): VdomElement = {
    val err = sxs.state.currentErrMessage
    Modal(size = "lg", show = err.nonEmpty, onHide = (_: Unit) => setErrorMessage(None))(
      ModalHeader(closeButton = true)(
        ModalTitle()("Error")
      ),
      ModalBody()(
        err
      )
    )
  }

  private def onChangeName(e: ReactEventFromInput): Callback = {
    e.persist()
    bs.modState(old => old.copy(state = old.state.copy(podName = e.target.value)))
  }

  private def handleKey(e: ReactKeyboardEvent): Callback =
    handleEsc(() => setDialogShownFlag(false)).orElse(handleEnter(() =>
      bs.state.flatMap(sxs => confirmPodCreation(sxs.state.user, sxs.state.podName)(e)))).orElse(ignoreKey)(e.keyCode)

  private def startPodCreation(e: ReactEventFromInput): Callback = setDialogShownFlag(true)

  private def setDialogShownFlag(flag: Boolean): Callback =
    bs.modState(sxs => sxs.copy(state = sxs.state.copy(dialogShown = flag)))

  private def setErrorMessage(err: Option[String]): Callback =
    bs.modState(sxs => sxs.copy(state = sxs.state.copy(currentErrMessage = err)))

  private def confirmPodCreation(user: User, podName: String)(e: ReactEvent): Callback = Callback.future {
    ResourceService.createSpotPod(user, podName).map(_.fold(err => setErrorMessage(Some(err)), _ => setErrorMessage(None)))
  }.flatMap(_ => setDialogShownFlag(false))

  private def confirmPodDeletion(user: User)(e: ReactEventFromInput): Callback = Callback.future {
    ResourceService.deleteSpotPod(user).map(_.fold(err => setErrorMessage(Some(err)), _ => setErrorMessage(None)))
  }.flatMap(_ => setDialogShownFlag(false))
}

object TopNav extends SessionTracker[Unit, State, Backend] {
  private val component = ScalaComponent
    .builder[Unit]("TopNav")
    .initialState(StateXSession(State(User(IRI.BlankNodeIRI.innerUri, List())), None))
    .renderBackend[Backend]
    .componentDidMount(trackSessionOn(fetchState))
    .componentWillUnmountConst(trackSessionOff())
    .configure(Reusability.shouldComponentUpdate)
    .build

  def apply(): Unmounted[Unit, StateXSession[State], Backend] = component()

  private def fetchState(session: Session): Future[State] = UserService.fetchUser(session.webId).map(State(_))
}
