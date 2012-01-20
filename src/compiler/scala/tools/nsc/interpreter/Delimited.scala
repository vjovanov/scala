/* NSC -- new Scala compiler
 * Copyright 2005-2011 LAMP/EPFL
 * @author Paul Phillips
 */

package scala.tools.nsc
package interpreter

import jline.console.completer.ArgumentCompleter.{ ArgumentDelimiter, ArgumentList }

class JLineDelimiter extends ArgumentDelimiter {
  def toJLine(args: List[String], cursor: Int) = args match {
    case Nil    => new ArgumentList(new Array[String](0), 0, 0, cursor)
    case xs     => new ArgumentList(xs.toArray, xs.size - 1, xs.last.length, cursor)
  }
  // jline 1.0
  def delimit(buffer: CharSequence, cursor: Int): ArgumentList = {
    delimit("" + buffer, cursor)
  }
  def isDelimiter(buffer: CharSequence, cursor: Int): Boolean = {
    isDelimiter("" + buffer ,cursor)
  }
  // jline 2.6
  def delimit(buffer: String, cursor: Int): ArgumentList = {
    val p = Parsed(buffer, cursor)
    toJLine(p.args, cursor)
  }
  def isDelimiter(buffer: String, cursor: Int): Boolean = Parsed(buffer, cursor).isDelimiter
}

trait Delimited {
  self: Parsed =>

  def delimited: Char => Boolean
  def escapeChars: List[Char] = List('\\')
  def quoteChars: List[(Char, Char)] = List(('\'', '\''), ('"', '"'))

  /** Break String into args based on delimiting function.
   */
  protected def toArgs(s: String): List[String] =
    if (s == "") Nil
    else (s indexWhere isDelimiterChar) match {
      case -1   => List(s)
      case idx  => (s take idx) :: toArgs(s drop (idx + 1))
    }

  def isDelimiterChar(ch: Char) = delimited(ch)
  def isEscapeChar(ch: Char): Boolean = escapeChars contains ch
  def isQuoteStart(ch: Char): Boolean = quoteChars map (_._1) contains ch
  def isQuoteEnd(ch: Char): Boolean = quoteChars map (_._2) contains ch
}
