<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:wfw="http://wellformedweb.org/CommentAPI/">	
	<xsl:output method="text" encoding="UTF-8" />
<!-- Main template -->
<xsl:template match="/">
<!-- Get loading value -->
<xsl:for-each select="/pmd/file">
	<xsl:for-each select="violation">
		<xsl:value-of select="../@name"/>
		<xsl:text>:</xsl:text>
		<xsl:value-of select="@beginline"/>
		<xsl:text>:  </xsl:text>
		<xsl:value-of select="normalize-space(@rule)"/><xsl:text> </xsl:text><xsl:value-of select="normalize-space(.)"/><xsl:text>
</xsl:text>
	</xsl:for-each>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>
