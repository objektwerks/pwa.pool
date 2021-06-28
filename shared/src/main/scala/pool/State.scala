package pool

sealed trait State extends Product with Serializable

final case class Id(id: Int) extends State

final case class Count(count: Int) extends State

final case class Pools(pools: Array[Pool]) extends State

final case class Surfaces(surfaces: Array[Surface]) extends State

final case class Pumps(pumps: Array[Pump]) extends State

final case class Timers(timers: Array[Timer]) extends State

final case class TimerSettings(timerSettings: Array[TimerSetting]) extends State

final case class Heaters(heaters: Array[Heater]) extends State

final case class HeaterSettings(heaterSettings: Array[HeaterSetting]) extends State

final case class Measurements(measurements: Array[Measurement]) extends State

final case class Cleanings(cleanings: Array[Cleaning]) extends State

final case class Chemicals(chemicals: Array[Chemical]) extends State

final case class Supplies(supplies: Array[Supply]) extends State

final case class Repairs(repairs: Array[Repair]) extends State