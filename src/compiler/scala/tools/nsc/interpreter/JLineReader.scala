/* NSC -- new Scala compiler
 * Copyright 2005-2011 LAMP/EPFL
 * @author Stepan Koltsov
 */

package scala.tools.nsc
package interpreter

import jline.ConsoleReader
import jline.{ History => _, _ }
import session._
import scala.collection.JavaConverters._
import Completion._
import io.Streamable.slurp

/**
 *  Reads from the console using JLine.
 */
class JLineReader(_completion: => Completion) extends InteractiveReader {
  val interactive   = true
  val consoleReader = new JLineConsoleReader()
  val history: JLineHistory = try JLineHistory() catch { case _: Exception => null }
  if (history ne null)
    consoleReader setHistory history

  lazy val completion = _completion
  lazy val keyBindings =
    try KeyBinding parse slurp(term.getDefaultBindings)
    catch { case _: Exception => Nil }

  def term    = consoleReader.getTerminal()
  def reset() = Terminal.resetTerminal()
  def init()  = term.initializeTerminal()

  def scalaToJline(tc: ScalaCompleter): Completor = new Completor {
    def complete(_buf: String, cursor: Int, candidates: JList[_]): Int = {
      val buf   = if (_buf == null) "" else _buf
      val Candidates(newCursor, newCandidates) = tc.complete(buf, cursor)
      newCandidates foreach ((x: Any) => candidates.asInstanceOf[JList[Any]].add(x))
      newCursor
    }
  }

  class JLineConsoleReader extends ConsoleReader with ConsoleReaderHelper {
    def readOneKey(prompt: String) = {
      this.printString(prompt)
      this.flushConsole()
      this.readVirtualKey()
    }
    def eraseLine() = while (consoleReader.delete()) { }
    def redrawLineAndFlush(): Unit = { 
      flushConsole()
      // redrawLine()
      drawLine()
      flushConsole()
    }
    // A hook for running code after the repl is done initializing.
    lazy val postInit: Unit = {
      this setBellEnabled false

      if (completion ne NoCompletion) {
        val argCompletor: ArgumentCompletor =
          new ArgumentCompletor(scalaToJline(completion.completer()), new JLineDelimiter)
        argCompletor setStrict false
        this addCompletor argCompletor
        this setAutoprintThreshhold 400 // max completion candidates without warning
      }
    }
  }

  def currentLine                 = consoleReader.getCursorBuffer.toString
  def redrawLine()                = consoleReader.redrawLineAndFlush()
  def eraseLine()                 = consoleReader.eraseLine()
  def readOneLine(prompt: String) = consoleReader readLine prompt
  def readOneKey(prompt: String)  = consoleReader readOneKey prompt
}
