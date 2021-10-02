package pool.dialog

sealed trait Mode
case object New extends Mode
case object View extends Mode