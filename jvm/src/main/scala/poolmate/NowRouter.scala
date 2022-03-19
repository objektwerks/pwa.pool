package poolmate

import cask.main.Routes
import cask.model.Response

import java.time.Instant

final class NowRouter() extends Routes:
  @cask.get("/now")
  def index() = Response(Instant.now.toString)

  initialize()