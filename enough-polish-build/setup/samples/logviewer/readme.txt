##############################################
#                                            #
#        The RMS Logviewer                   #
#                                            #
##############################################


The Logviewer MIDlet can show and filter log messages that you 
have previously stored in the recordstore management system (RMS).

You can store log messages in the RMS by enabling the "rms" log handler 
in your application. Do so by adding the "rms" handler in the <debug> element 
in your build.xml file:

<debug level="error" showLogOnError="true" verbose="false" >
	<handler name="rms">
		<parameter name="useBackgroundThread" value="false" />
	</handler>
	<filter package="de.enough.polish.demo" level="debug"/>
</debug>

On MIDP/2.0 devices a shared recordstore is used, that's why you need
to set two preprocessing variables before you build the Logviewer:

polish.log.MIDletSuite: must contain the name of the MIDlet suite.
This corresponds to the "name" attribute in the <info> element in the
build.xml file of your application.

polish.log.Vendor: must contain the vendor name, usually the name of 
your company. This is the same as the "vendorName" attribute in the
<info> element of your application.

Example:

<variables>
	<variable name="polish.log.MIDletSuite" value="Demonstrator" />
	<variable name="polish.log.Vendor" value="Enough Software" />
</variables>
