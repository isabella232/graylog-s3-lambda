package org.graylog.integrations.s3;

import org.graylog2.gelfclient.GelfMessage;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CloudFlareLogsParserTest {

    private static final String RFC3339_TIMESTAMP_MESSAGE = "{\"CacheCacheStatus\":\"unknown\",\"CacheResponseBytes\":1502,\"CacheResponseStatus\":200,\"CacheTieredFill\":false,\"ClientASN\":7922,\"ClientCountry\":\"us\",\"ClientDeviceType\":\"desktop\",\"ClientIP\":\"2601:2c1:8501:2cab:1130:95b3:d3af:b33e\",\"ClientIPClass\":\"noRecord\",\"ClientRequestBytes\":956,\"ClientRequestHost\":\"sendafox.com:8080\",\"ClientRequestMethod\":\"GET\",\"ClientRequestPath\":\"/search\",\"ClientRequestProtocol\":\"HTTP/1.1\",\"ClientRequestReferer\":\"\",\"ClientRequestURI\":\"/search\",\"ClientRequestUserAgent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36\",\"ClientSSLCipher\":\"NONE\",\"ClientSSLProtocol\":\"none\",\"ClientSrcPort\":52039,\"EdgeColoCode\":\"DFW\",\"EdgeColoID\":15,\"EdgeEndTimestamp\":\"2019-09-13T20:23:09Z\",\"EdgePathingOp\":\"wl\",\"EdgePathingSrc\":\"macro\",\"EdgePathingStatus\":\"nr\",\"EdgeRateLimitAction\":\"\",\"EdgeRateLimitID\":0,\"EdgeRequestHost\":\"sendafox.com:8080\",\"EdgeResponseBytes\":705,\"EdgeResponseCompressionRatio\":2.48,\"EdgeResponseContentType\":\"text/html\",\"EdgeResponseStatus\":200,\"EdgeServerIP\":\"108.162.221.188\",\"EdgeStartTimestamp\":\"2019-09-13T20:23:09Z\",\"FirewallMatchesActions\":[],\"FirewallMatchesSources\":[],\"FirewallMatchesRuleIDs\":[],\"OriginIP\":\"34.229.66.141\",\"OriginResponseBytes\":0,\"OriginResponseHTTPExpires\":\"\",\"OriginResponseHTTPLastModified\":\"\",\"OriginResponseStatus\":200,\"OriginResponseTime\":57000000,\"OriginSSLProtocol\":\"unknown\",\"ParentRayID\":\"00\",\"RayID\":\"515cd65df9be9b42\",\"SecurityLevel\":\"med\",\"WAFAction\":\"unknown\",\"WAFFlags\":\"0\",\"WAFMatchedVar\":\"\",\"WAFProfile\":\"unknown\",\"WAFRuleID\":\"\",\"WAFRuleMessage\":\"\",\"WorkerCPUTime\":0,\"WorkerStatus\":\"unknown\",\"WorkerSubrequest\":false,\"WorkerSubrequestCount\":0,\"ZoneID\":175856242}";
    private static final String UNIX_TIMESTAMP_MESSAGE = "{\"CacheCacheStatus\":\"unknown\",\"CacheResponseBytes\":1141,\"CacheResponseStatus\":404,\"CacheTieredFill\":false,\"ClientASN\":7922,\"ClientCountry\":\"us\",\"ClientDeviceType\":\"desktop\",\"ClientIP\":\"2601:2c1:8501:2cab:95a7:539a:8cae:ee9f\",\"ClientIPClass\":\"noRecord\",\"ClientRequestBytes\":609,\"ClientRequestHost\":\"sendafox.com:8080\",\"ClientRequestMethod\":\"GET\",\"ClientRequestPath\":\"/api/system/inputs/9184F815-AC09-4355-9905-6F104DF0A50F\",\"ClientRequestProtocol\":\"HTTP/1.1\",\"ClientRequestReferer\":\"\",\"ClientRequestURI\":\"/api/system/inputs/9184F815-AC09-4355-9905-6F104DF0A50F\",\"ClientRequestUserAgent\":\"curl/7.54.0\",\"ClientSSLCipher\":\"NONE\",\"ClientSSLProtocol\":\"none\",\"ClientSrcPort\":52528,\"EdgeColoCode\":\"DFW\",\"EdgeColoID\":15,\"EdgeEndTimestamp\":1568923202,\"EdgePathingOp\":\"wl\",\"EdgePathingSrc\":\"macro\",\"EdgePathingStatus\":\"nr\",\"EdgeRateLimitAction\":\"\",\"EdgeRateLimitID\":0,\"EdgeRequestHost\":\"sendafox.com:8080\",\"EdgeResponseBytes\":511,\"EdgeResponseCompressionRatio\":1,\"EdgeResponseContentType\":\"application/json\",\"EdgeResponseStatus\":404,\"EdgeServerIP\":\"172.69.69.227\",\"EdgeStartTimestamp\":1568923202,\"FirewallMatchesActions\":[],\"FirewallMatchesSources\":[],\"FirewallMatchesRuleIDs\":[],\"OriginIP\":\"34.229.66.141\",\"OriginResponseBytes\":0,\"OriginResponseHTTPExpires\":\"\",\"OriginResponseHTTPLastModified\":\"\",\"OriginResponseStatus\":404,\"OriginResponseTime\":140000000,\"OriginSSLProtocol\":\"unknown\",\"ParentRayID\":\"00\",\"RayID\":\"518e24bfe8ca589b\",\"SecurityLevel\":\"med\",\"WAFAction\":\"unknown\",\"WAFFlags\":\"0\",\"WAFMatchedVar\":\"\",\"WAFProfile\":\"unknown\",\"WAFRuleID\":\"\",\"WAFRuleMessage\":\"\",\"WorkerCPUTime\":0,\"WorkerStatus\":\"unknown\",\"WorkerSubrequest\":false,\"WorkerSubrequestCount\":0,\"ZoneID\":175856242}";
    private static final String UNIX_NANO_TIMESTAMP_MESSAGE = "{\"CacheCacheStatus\":\"unknown\",\"CacheResponseBytes\":1143,\"CacheResponseStatus\":404,\"CacheTieredFill\":false,\"ClientASN\":7922,\"ClientCountry\":\"us\",\"ClientDeviceType\":\"desktop\",\"ClientIP\":\"2601:2c1:8501:2cab:95a7:539a:8cae:ee9f\",\"ClientIPClass\":\"noRecord\",\"ClientRequestBytes\":609,\"ClientRequestHost\":\"sendafox.com:8080\",\"ClientRequestMethod\":\"GET\",\"ClientRequestPath\":\"/api/system/inputs/7192DF59-A25B-4472-8CE4-65B3EFD7C900\",\"ClientRequestProtocol\":\"HTTP/1.1\",\"ClientRequestReferer\":\"\",\"ClientRequestURI\":\"/api/system/inputs/7192DF59-A25B-4472-8CE4-65B3EFD7C900\",\"ClientRequestUserAgent\":\"curl/7.54.0\",\"ClientSSLCipher\":\"NONE\",\"ClientSSLProtocol\":\"none\",\"ClientSrcPort\":63964,\"EdgeColoCode\":\"DFW\",\"EdgeColoID\":15,\"EdgeEndTimestamp\":1568924647190000000,\"EdgePathingOp\":\"wl\",\"EdgePathingSrc\":\"macro\",\"EdgePathingStatus\":\"nr\",\"EdgeRateLimitAction\":\"\",\"EdgeRateLimitID\":0,\"EdgeRequestHost\":\"sendafox.com:8080\",\"EdgeResponseBytes\":511,\"EdgeResponseCompressionRatio\":1,\"EdgeResponseContentType\":\"application/json\",\"EdgeResponseStatus\":404,\"EdgeServerIP\":\"108.162.221.188\",\"EdgeStartTimestamp\":1568924647030000000,\"FirewallMatchesActions\":[],\"FirewallMatchesSources\":[],\"FirewallMatchesRuleIDs\":[],\"OriginIP\":\"34.229.66.141\",\"OriginResponseBytes\":0,\"OriginResponseHTTPExpires\":\"\",\"OriginResponseHTTPLastModified\":\"\",\"OriginResponseStatus\":404,\"OriginResponseTime\":125000000,\"OriginSSLProtocol\":\"unknown\",\"ParentRayID\":\"00\",\"RayID\":\"518e4803ff269b85\",\"SecurityLevel\":\"med\",\"WAFAction\":\"unknown\",\"WAFFlags\":\"0\",\"WAFMatchedVar\":\"\",\"WAFProfile\":\"unknown\",\"WAFRuleID\":\"\",\"WAFRuleMessage\":\"\",\"WorkerCPUTime\":0,\"WorkerStatus\":\"unknown\",\"WorkerSubrequest\":false,\"WorkerSubrequestCount\":0,\"ZoneID\":175856242}";

    @Test
    public void testParsing() throws IOException {

        GelfMessage gelfMessage = CloudFlareLogsParser.parseMessage(RFC3339_TIMESTAMP_MESSAGE, "a-host", Config.newInstance());
        assertEquals(Double.valueOf(1568406189), Double.valueOf(gelfMessage.getTimestamp()));
        assertEquals(59, gelfMessage.getAdditionalFields().size());
    }

    @Test
    public void testUnixParsing() throws IOException {

        GelfMessage gelfMessage = CloudFlareLogsParser.parseMessage(UNIX_TIMESTAMP_MESSAGE, "a-host", Config.newInstance());
        assertEquals(Double.valueOf(1568406189), Double.valueOf(gelfMessage.getTimestamp()));
        assertEquals(59, gelfMessage.getAdditionalFields().size());
    }

    @Test
    public void testUnixNanoParsing() throws IOException {

        GelfMessage gelfMessage = CloudFlareLogsParser.parseMessage(UNIX_NANO_TIMESTAMP_MESSAGE, "a-host", Config.newInstance());
        assertEquals(Double.valueOf(1568924647.0300002), Double.valueOf(gelfMessage.getTimestamp()));
        assertEquals(59, gelfMessage.getAdditionalFields().size());
    }
}
