package suzaku.ui.style

import boopickle.{PickleState, Pickler, UnpickleState}

// Style classes
abstract class StyleClass extends StylePropOrClass {
  def styleDefs: List[StyleDef]

  def id: Int
}

case class PureStyleClass(override val id: Int) extends StyleClass {
  def styleDefs: List[StyleDef] = Nil
}

case class StyleClassRegistration(id: Int, className: String, styleProps: List[StyleProperty])

class StyleClassPickler extends Pickler[StyleClass] {
  override def pickle(obj: StyleClass)(implicit state: PickleState): Unit = {
    state.enc.writeInt(obj.id)
  }

  override def unpickle(implicit state: UnpickleState): StyleClass = {
    PureStyleClass(state.dec.readInt)
  }
}
