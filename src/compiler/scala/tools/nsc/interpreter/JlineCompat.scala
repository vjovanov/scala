/* NSC -- new Scala compiler
 * Copyright 2005-2011 LAMP/EPFL
 * @author Paul Phillips
 */

package scala.tools.nsc
package interpreter

import jline.Terminal
import jline.console.ConsoleReader
import jline.console.history.{ History => JHistory, FileHistory => JFileHistory }
import collection.JavaConverters._

/** Compatibility layer to ease struggle with multiple
 *  versions of jline.  They include selected methods which were in
 *  jline 1.0 and aren't in 2.6, so the client code can
 *  continue to call the old methods against 2.6.
 */
trait JlineCompat {
  private[interpreter] implicit def jlineCharSeqColl(xs: JCollection[_ <: CharSequence]): List[String] =
    xs.asScala.toList map ("" + _)
  private[interpreter] implicit def jlineTerminalCompat(x: Terminal) =
    new JlineTerminalWrapper(x)
  private[interpreter] implicit def jlineConsoleCompat(x: ConsoleReader) =
    new JlineConsoleReaderWrapper(x)
}

trait Jline26Terminal extends Terminal {
  def init(): Unit
  def restore(): Unit
  def reset(): Unit
  def isSupported(): Boolean
  def getWidth(): Int
  def getHeight(): Int
  def isAnsiSupported(): Boolean
  def wrapOutIfNeeded(out: OutputStream): OutputStream
  def wrapInIfNeeded(in: InputStream): InputStream
  def hasWeirdWrap(): Boolean
  def isEchoEnabled(): Boolean
  def setEchoEnabled(enabled: Boolean): Unit
}

class JlineTerminalWrapper(term: Terminal) {
  import term._
  def resetTerminal(): Unit      = reset()
  def initializeTerminal(): Unit = init()
  def getTerminalWidth(): Int    = getWidth()
  def getTerminalHeight(): Int   = getHeight()

  // def init()              = term.initializeTerminal()
  // def reset()             = term.setupTerminal()
  // def getTerminalWidth()  = term.getWidth()
  // def getTerminalHeight() = term.getHeight()
}

class JlineConsoleReaderWrapper(reader: ConsoleReader) {
  import reader._

  def setUsePagination(value: Boolean)       = setPaginationEnabled(value)
  def getUsePagination()                     = isPaginationEnabled
  def flushConsole()                         = flush()
  def getDefaultPrompt()                     = getPrompt()
  def setAutoprintThreshhold(threshold: Int) = setAutoprintThreshold(threshold)
  def getAutoprintThreshhold()               = getAutoprintThreshold()
  def printString(s: String)                 = print(s)
  def readVirtualKey()                       = readCharacter()
}

trait JlineHistoryWrapperTrait extends JFileHistory {
  def addToHistory(line: String): Unit = add(line)
  def flushBuffer(): Unit              = flush()
  def getCurrentIndex()                = index
  def getHistory(index: Int): String   = "" + get(index)
  def getHistoryList()                 = entries()
  def moveToFirstEntry(): Boolean      = moveToFirst()
  def moveToLastEntry(): Boolean       = moveToLast()
}
