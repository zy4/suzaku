package suzaku.platform.web.ui

import org.scalajs.dom
import suzaku.platform.web.{DOMWidget, DOMWidgetArtifact}
import suzaku.ui.{LinearLayoutProtocol, WidgetBuilder}

class DOMLinearLayout(context: LinearLayoutProtocol.ChannelContext)
    extends DOMWidget[LinearLayoutProtocol.type, dom.html.Div] {
  import LinearLayoutProtocol._
  import Direction._
  import Justify._

  val artifact = {
    import scalatags.JsDom.all._
    val el = div().render
    el.style.setProperty("display", "flex")
    DOMWidgetArtifact(el)
  }

  val updateDirection = updateStyleProperty[Direction](artifact.el, "flex-direction", (value, set, remove) => value match {
    case Horizontal    => remove()
    case HorizontalRev => set("row-reverse")
    case Vertical      => set("column")
    case VerticalRev   => set("column-reverse")
  }) _

  val updateJustify = updateStyleProperty[Justify](artifact.el, "justify-content", (value, set, remove) => value match {
    case Start        => remove()
    case End          => set("flex-end")
    case Center       => set("center")
    case SpaceBetween => set("space-between")
    case SpaceAround  => set("space-around")
  }) _


  updateDirection(context.direction)
  updateJustify(context.justify)

  override def setChildren(children: Seq[Artifact]) = {
    import org.scalajs.dom.ext._
    modifyDOM { el =>
      el.childNodes.foreach(el.removeChild)
      children.foreach { c =>
        el.appendChild(c.el)
      }
    }
  }

  override def process = {
    case SetDirection(direction) =>
      updateDirection(direction)
    case SetJustify(justify) =>
      updateJustify(justify)
  }
}

object DOMLinearLayoutBuilder extends WidgetBuilder(LinearLayoutProtocol) {
  import LinearLayoutProtocol._

  override protected def create(context: ChannelContext) = {
    new DOMLinearLayout(context)
  }
}