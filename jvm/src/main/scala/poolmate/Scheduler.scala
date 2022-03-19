package poolmate

import java.util.concurrent._

object Scheduler:
  val initialDelay = 10L
  val period = 60L

final class Scheduler(emailProcessor: EmailProcessor):
  import Scheduler.*
  
  val executor = new ScheduledThreadPoolExecutor(1)
  val task = emailProcessor.process()
  val scheduler = executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS)
  scheduler.cancel(false)