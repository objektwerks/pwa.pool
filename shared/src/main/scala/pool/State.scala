package pool

sealed trait State extends Product with Serializable

final case class Id(id: Int) extends State

final case class Count(count: Int) extends State

final case class Pools(pools: Seq[Pool]) extends State

final case class Surfaces(surfaces: Seq[Surface]) extends State

final case class Pumps(pumps: Seq[Pump]) extends State

final case class Timers(timers: Seq[Timer]) extends State

final case class TimerSettings(timerSettings: Seq[TimerSetting]) extends State

final case class Heaters(heaters: Seq[Heater]) extends State

final case class HeaterSettings(heaterSettings: Seq[HeaterSetting]) extends State

final case class Measurements(measurements: Seq[Measurement]) extends State

final case class Cleanings(cleanings: Seq[Cleaning]) extends State

final case class Chemicals(chemicals: Seq[Chemical]) extends State

final case class Supplies(supplies: Seq[Supply]) extends State

final case class Repairs(repairs: Seq[Repair]) extends State