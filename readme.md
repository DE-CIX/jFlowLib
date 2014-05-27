jFlowlib
========
 
jFlowLib is a Java library to parse and generate sFlow and IPFIX data. For this,
jFlowLib consists of two parts:
 
**jsFlow:** jsFlow is Java library for [sFlow](http://www.sflow.org/) (version 5). sFlow is an industry standard to
monitor switches and routers. 

So far, jsFlow implements all the headers as used by Force10 E-series switches. However,
the jsFlow architecture is designed to be easily extensible which means it should be easy
to implement support for additional vendors and switch models.
 
**jIPFIX:** jIPFIX is Java library for [IPFIX](http://tools.ietf.org/wg/ipfix/). IPFIX is an IETF protocol and it was
created based on the need for a common, universal standard of export for Internet
Protocol flow information from routers, probes and other devices that are used by
mediation systems, accounting/billing systems and network management systems to
facilitate services such as measurement, accounting and billing. 
 
The current version of jIPFIX supports all headers as used by the Alcatel Lucent
7750 configured to export IPFIX based on the L2-IP flow template. However, the jIPFIX
architecture is designed to be easily extensible so that other flow templates can be
easily added.
 
 
Which use cases are covered by jFlowLib?
----------------------------------------
 
The abstract use case for jFlowLib is to work with sFlow and IPFIX data. jFlowLib can be
used as sFlow agent or IPFIX exporter and as sFlow or IPFIX collector. Additional and
more specific use cases are listed in the following:
 
* Transparent sFlow or IPFIX proxy: In case you want to modify the sFlow or IPFIX data
that is sent by switches or routers jFlowLib can be used as a transparent sFlow or IPFIX
proxy.
* sFlow or IPFIX multiplexer: Typically, routers and switches support only a limited
number of sFlow or IPFIX collecters. In case the sFlow or IPFIX traffic should be
received by more than this number of sFlow or IPFIX collectors a multiplexer can be used
to distribute the sFlow or IPFIX data to any number of sFlow or IPFIX collectors.


What do you need to run jFlowLib?
---------------------------------
A Java Runtime Environment (version 6 or higher) is required to be able to use the
jFlowLib library.


Under which License is jFlowLib available?
------------------------------------------
The jFlowLib library is released under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).


Who develops and maintains the jFlowLib library?
------------------------------------------------
The initial version of the jFlowLib library has been developed during an internal
project at the [DE-CIX Management GmbH](https://www.de-cix.net).