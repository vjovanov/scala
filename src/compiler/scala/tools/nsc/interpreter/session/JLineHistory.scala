/* NSC -- new Scala compiler
 * Copyright 2005-2011 LAMP/EPFL
 * @author Paul Phillips
 */

package scala.tools.nsc
package interpreter
package session

/** A straight scalification of the jline interface which mixes
 *  in the sparse jline-independent one too.
 */
trait JLineHistory extends jline.History with History {
  def size: Int
  def clear(): Unit
  def flushBuffer(): Unit
  def getHistory(index: Int): String
  def addToHistory(line: String): Unit
  def getMaxSize(): Int
  def setMaxSize(maxSize: Int): Unit
  // def getHistoryList(): JList[_]
  def getCurrentIndex(): Int
  def current(): String
  def previous(): Boolean
  def next(): Boolean
  def moveToFirstEntry(): Boolean
  def moveToLastEntry(): Boolean
  def moveToEnd(): Unit
  def searchBackwards(searchTerm: String): Int
  def searchBackwards(searchTerm: String, startIndex: Int): Int
}

object JLineHistory {
  import scala.util.Properties._
  def defaultFile = new java.io.File(userHome, ".scala_history")

  def apply(historyFile: java.io.File = defaultFile): JLineHistory = {
    new FileHistory(historyFile)
  }
}


class FileHistory(historyFile: java.io.File) extends jline.History(historyFile.getAbsoluteFile()) with SimpleHistory {
  repldbg("Setting history file to " + historyFile)

  // override def addToHistory(buffer: String) {
  //   println("Add to history: " + buffer)
  //   super.addToHistory(buffer)
  // }
}
