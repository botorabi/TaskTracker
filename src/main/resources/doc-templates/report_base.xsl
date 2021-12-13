<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id$ -->
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
	<xsl:param name="versionParam" select="'1.0'"/> 

	<!-- root: report-->
	<xsl:template match="report">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="A4"
										page-width="210mm" page-height="297mm"
										margin-top="1cm"   margin-bottom="1cm"
										margin-left="1cm"  margin-right="1cm">
					<fo:region-body   margin="1.2cm"/>
					<fo:region-before extent="1cm"/>
					<fo:region-after  extent="1cm"/>
					<fo:region-start  extent="0.7cm"/>
					<fo:region-end    extent="0.7cm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<xsl:apply-templates select="cover_page"/>
			<xsl:apply-templates select="standard_page"/>
		</fo:root>
	</xsl:template>


	<!-- root child: cover_page -->
	<xsl:template match="cover_page">
		<fo:page-sequence master-reference="A4">
			<fo:flow flow-name="xsl-region-body">
				<fo:block-container height="80%" width="100%" display-align="center">
					<fo:block space-after="30pt" text-align="center" linefeed-treatment="preserve">
						<fo:inline font-weight="bold" font-size="18pt">
							<xsl:value-of select="title"/>
						</fo:inline>
					</fo:block>
					<fo:block space-after="20pt" line-height="3em" text-align="center" linefeed-treatment="preserve">
						<fo:inline font-size="16pt">
							<xsl:value-of select="subtitle"/>
						</fo:inline>
					</fo:block>
				</fo:block-container>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>

	<!-- root child: standard_page -->
	<xsl:template match="standard_page">
		<fo:page-sequence master-reference="A4">
			<xsl:apply-templates select="header"/>
			<xsl:apply-templates select="footer"/>
			<fo:flow flow-name="xsl-region-body">
				<fo:block-container font-size="12pt">
					<xsl:apply-templates select="content"/>
				</fo:block-container>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>


	<!-- standard_page child: header -->
	<xsl:template match="header">
		<fo:static-content flow-name="xsl-region-before">
			<fo:block font-size="8pt" text-align="right" border-bottom="solid 0.1mm">
				<xsl:value-of select="text"/>
			</fo:block>
		</fo:static-content>
	</xsl:template> 


	<!-- standard_page child: footer -->
	<xsl:template match="footer">
		<fo:static-content flow-name="xsl-region-after">
			<fo:block font-size="8pt" text-align="right" border-top="solid 0.1mm">
				<xsl:value-of select="text"/>
			</fo:block>
		</fo:static-content>
	</xsl:template>

	<!-- standard_page child: content-->
	<xsl:template match="content">
		<fo:block linefeed-treatment="preserve">
			<xsl:choose>
				<xsl:when test="image">
					<fo:external-graphic src="{image}" content-width="scale-down-to-fit"  width="100%"/>
				</xsl:when>
				<xsl:when test="text">
					<xsl:value-of select="text"/>
				</xsl:when>
				<xsl:when test="contenttitle">
					<fo:block  font-weight="bold" font-size="16pt" linefeed-treatment="preserve">
						<xsl:value-of select="contenttitle"/>
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose> 
		</fo:block>	
	</xsl:template>

</xsl:stylesheet>
