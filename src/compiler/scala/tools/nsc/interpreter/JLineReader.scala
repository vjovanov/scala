/* NSC -- new Scala compiler
 * Copyright 2005-2011 LAMP/EPFL
 * @author Stepan Koltsov
 */

package scala.tools.nsc
package interpreter

import jline.console.ConsoleReader
import jline.console.completer._
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
  // lazy val keyBindings =
  //   try KeyBinding parse slurp(term.getDefaultBindings)
  //   catch { case _: Exception => Nil }

  def term    = consoleReader.getTerminal()
  def reset() = term.resetTerminal()
  def init()  = term.initializeTerminal()

  def scalaToJline(tc: ScalaCompleter): Completer = new Completer {
    // def complete(_buf: String, cursor: Int, candidates: JList[_]): Int = {
    def complete(_buf: String, cursor: Int, candidates: JList[CharSequence]): Int = {
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
      this.flushConsole()
      // redrawLine()
      this.drawLine()
      this.flushConsole()
    }
    // A hook for running code after the repl is done initializing.
    lazy val postInit: Unit = {
      sys.props("jline.nobell") = "true"
      // this setBellEnabled false

      if (completion ne NoCompletion) {
        val argCompleter: ArgumentCompleter =
          new ArgumentCompleter(new JLineDelimiter, scalaToJline(completion.completer()))
          // new ArgumentCompleter(scalaToJline(completion.completer()), new JLineDelimiter)
        argCompleter setStrict false
        this addCompleter argCompleter
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
