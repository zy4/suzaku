package suzaku.ui

import arteria.core._
import suzaku.ui.UIProtocol.UIChannel
import suzaku.ui.layout.LayoutProperty
import suzaku.ui.style.{StyleBaseProperty, StyleClass, StyleClasses, StyleDef, StylePropOrClass, StyleProperty, StyleSeq}

trait WidgetBlueprint extends Blueprint {
  type P <: Protocol
  type This <: WidgetBlueprint
  type Proxy <: WidgetProxy[P, This]

  private[suzaku] var _style  = Map.empty[Class[_], StyleProperty]
  private[suzaku] var _layout = List.empty[LayoutProperty]

  def createProxy(viewId: Int, uiChannel: UIChannel): Proxy

  def children: List[Blueprint] = Nil

  def sameAs(that: This): Boolean = equals(that) && _style == that._style

  @noinline final def <<(styleProperty: StylePropOrClass*): this.type = {
    var styleClasses = List.empty[StyleClass]
    val styles = styleProperty.flatMap {
      case StyleSeq(seq)        => seq
      case s: StyleBaseProperty => s :: Nil
      case c: StyleClass =>
        styleClasses ::= c
        Nil
      case _ => Nil
    }
    _style ++= styles.map(p => (p.getClass, p))
    if (styleClasses.nonEmpty)
      _style += (classOf[StyleClasses] -> StyleClasses(styleClasses.reverse))
    this
  }

  @inline final def withLayout(layoutProperty: LayoutProperty*): this.type = {
    _layout = _layout ::: layoutProperty.toList
    this
  }
}

object WidgetProtocol extends Protocol {
  import boopickle.Default._

  implicit val stylePropertyPickler  = StyleProperty.stylePickler
  implicit val layoutPropertyPickler = LayoutProperty.layoutPickler

  sealed trait WidgetMessage extends Message

  case class UpdateStyle(params: List[(StyleProperty, Boolean)]) extends WidgetMessage

  case class UpdateLayout(params: List[LayoutProperty]) extends WidgetMessage

  val wmPickler = compositePickler[WidgetMessage]
    .addConcreteType[UpdateStyle]
    .addConcreteType[UpdateLayout]

  override type ChannelContext = Unit

  implicit val (messagePickler, witnessMsg) = defineProtocol(wmPickler)

  implicit val contextPickler = implicitly[Pickler[Unit]]
}

trait WidgetBlueprintProvider {
  def blueprintClass: Class[_ <: WidgetBlueprint]
}
