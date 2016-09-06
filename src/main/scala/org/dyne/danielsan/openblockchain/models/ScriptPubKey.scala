package org.dyne.danielsan.openblockchain.models

/**
  * Created by dan_mi_sun on 12/05/2016.
  */
case class ScriptPubKey(hex: String,
                        asm: String,
                        `type`: String,
                        reqSigs: Option[Int],
                        addresses: List[String])
