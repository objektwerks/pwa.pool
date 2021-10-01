package pool.dialog

sealed trait Mode
case object Add extends Mode
case object Edit extends Mode
case object View extends Mode