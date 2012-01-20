/* NSC -- new Scala compiler
 * Copyright 2005-2011 LAMP/EPFL
 * @author Paul Phillips
 */

package scala.tools.nsc
package interpreter
package session

import scala.collection.JavaConverters._

case class Entry(index: Int, value: String) {
  override def toString = value
}

trait SimpleHistory extends JLineHistory { 
  def asStrings       = getHistoryList.asScala.toList.asInstanceOf[List[String]]
  def grep(s: String) = asStrings filter (_ contains s)
}
