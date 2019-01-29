package me.benetis.shared.api

import me.benetis.shared.DiscussionLength

trait ApiForFrontend {
  def getDiscussionLengths(): DiscussionLength
}
