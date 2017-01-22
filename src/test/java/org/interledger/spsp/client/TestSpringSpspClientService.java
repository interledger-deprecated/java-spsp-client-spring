package org.interledger.spsp.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.interledger.cryptoconditions.uri.CryptoConditionUri;
import org.interledger.ilp.core.InterledgerAddress;
import org.interledger.spsp.core.SpspService;
import org.interledger.spsp.core.model.Invoice;
import org.interledger.spsp.core.model.InvoiceStatus;
import org.interledger.spsp.core.model.Payee;
import org.interledger.spsp.core.model.PaymentRequest;
import org.interledger.spsp.core.model.Receiver;
import org.interledger.spsp.core.model.ReceiverType;
import org.interledger.spsp.rest.json.ConditionSerializer;
import org.interledger.spsp.rest.json.InterledgerAddressSerializer;
import org.interledger.spsp.rest.json.JsonInvoice;
import org.interledger.spsp.rest.json.JsonPayee;
import org.interledger.spsp.rest.json.JsonPaymentRequest;
import org.interledger.spsp.rest.json.JsonRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class TestSpringSpspClientService {

  private RestTemplate restTemplate;

  private SpspService service;

  private MockRestServiceServer mockServer;

  private ObjectMapper mapper;

  /**
   * Pre-test setup.
   */
  @Before
  public void setup() {
    restTemplate = new RestTemplate();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    service = new SpringSpspClientService(restTemplate);

    mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    // make sure serialization handles JSR310 correctly
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    // inject a custom serializers for interledger address and crypto conditions since jacksons
    // introspection gets it pretty wrong
    SimpleModule module = new SimpleModule();
    module.addSerializer(new InterledgerAddressSerializer());
    module.addSerializer(new ConditionSerializer());
    mapper.registerModule(module);
  }

  @Test
  public void test_query_Payee() throws JsonProcessingException, MalformedURLException {
    JsonPayee mockPayee = new JsonPayee();
    mockPayee.setName("Bob Dylan");
    mockPayee.setType(ReceiverType.payee);
    mockPayee.setCurrencyCode("USD");
    mockPayee.setCurrencySymbol("$");
    mockPayee.setAccount(new InterledgerAddress("ilpdemo.red.bob"));
    mockPayee.setImageUrl(new URL("https://red.ilpdemo.org/api/receivers/bob/profile_pic.jpg"));

    mockServer.expect(requestTo("http://red.ilpdemo.org/api/receivers/bob"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(mapper.writeValueAsString(mockPayee), MediaType.APPLICATION_JSON));

    Receiver response = service.query(URI.create("http://red.ilpdemo.org/api/receivers/bob"));

    mockServer.verify();
    assertNotNull(response);
    assertTrue(response instanceof Payee);
    Payee payeeResponse = (Payee) response;
    assertEquals(mockPayee.getAccount(), payeeResponse.getAccount());
    assertEquals(ReceiverType.payee, payeeResponse.getType());
    assertEquals(mockPayee.getCurrencyCode(), payeeResponse.getCurrencyCode());
    assertEquals(mockPayee.getCurrencySymbol(), payeeResponse.getCurrencySymbol());
    assertEquals(mockPayee.getImageUrl(), payeeResponse.getImageUrl());
  }

  @Test
  public void test_query_Invoice() throws JsonProcessingException {
    JsonInvoice mockInvoice = new JsonInvoice();
    mockInvoice.setType(ReceiverType.invoice);
    mockInvoice.setCurrencyCode("USD");
    mockInvoice.setCurrencySymbol("$");
    mockInvoice.setAmount("10.40");
    mockInvoice.setStatus(InvoiceStatus.unpaid);
    mockInvoice.setAccount(new InterledgerAddress("ilpdemo.red.amazon.111-7777777-1111111"));
    mockInvoice.setInvoiceInfo(URI.create(
        "https://www.amazon.com/gp/your-account/order-details?ie=UTF8&orderID=111-7777777-1111111"));

    mockServer.expect(requestTo("http://red.ilpdemo.org/api/receivers/amazon/111-7777777-1111111"))
        .andExpect(method(HttpMethod.GET)).andRespond(
            withSuccess(mapper.writeValueAsString(mockInvoice), MediaType.APPLICATION_JSON));

    Receiver response = service
        .query(URI.create("http://red.ilpdemo.org/api/receivers/amazon/111-7777777-1111111"));

    mockServer.verify();
    assertNotNull(response);
    assertTrue(response instanceof Invoice);
    Invoice invoiceResponse = (Invoice) response;
    assertEquals(ReceiverType.invoice, invoiceResponse.getType());
    assertEquals(mockInvoice.getAccount(), invoiceResponse.getAccount());
    assertEquals(mockInvoice.getCurrencyCode(), invoiceResponse.getCurrencyCode());
    assertEquals(mockInvoice.getCurrencySymbol(), invoiceResponse.getCurrencySymbol());
    assertEquals(mockInvoice.getAmount(), invoiceResponse.getAmount());
    assertEquals(mockInvoice.getStatus(), invoiceResponse.getStatus());
    assertEquals(mockInvoice.getInvoiceInfo(), invoiceResponse.getInvoiceInfo());
  }

  @Test
  public void test_SetupPayment_payee() throws Exception {
    JsonRequest req = new JsonRequest();
    req.setAmount("10.40");
    req.setSenderIdentifier("alice@blue.ilpdemo.org");
    req.setMemo("Hey Bob!");

    JsonPaymentRequest reqRsp = new JsonPaymentRequest();
    reqRsp.setAddress(new InterledgerAddress(
        "ilpdemo.red.bob.b9c4ceba-51e4-4a80-b1a7-2972383e98af"));
    
    reqRsp.setAmount("10.40");
    reqRsp.setExpiresAt(ZonedDateTime.of(2016, 8, 16, 12, 0, 0, 0, ZoneId.of("UTC")));
    reqRsp.setAdditionalHeaders("asdf98zxcvlknannasdpfi09qwoijasdfk09xcv009as7zxcv");

    Map<String, String> reqRspData = new HashMap<>();
    reqRspData.put("sender_identifier", "alice@blue.ilpdemo.org");
    reqRsp.setData(reqRspData);
    
    reqRsp.setCondition(CryptoConditionUri.parse(URI.create(
        "ni:///sha-256;47DEQpj8HBSa-_TImW-5JCeuQeRkm5NMpJWZG3hSuFU?fpt=preimage-sha-256&cost=0")));

    mockServer.expect(requestTo("http://red.ilpdemo.org/api/receiver/bob"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(content().bytes(mapper.writeValueAsBytes(req)))
        .andRespond(withSuccess(mapper.writeValueAsString(reqRsp), MediaType.APPLICATION_JSON));

    PaymentRequest response = service.setupPayment(
        URI.create("http://red.ilpdemo.org/api/receiver/bob"), "10.40", "alice@blue.ilpdemo.org",
        "Hey Bob!");

    assertNotNull(response);
    assertEquals(reqRsp.getAddress(), response.getAddress());
    assertEquals(reqRsp.getAmount(), response.getAmount());
    assertEquals(reqRsp.getExpiresAt(), response.getExpiresAt());
    assertEquals(reqRsp.getAdditionalHeaders(), response.getAdditionalHeaders());
    assertEquals(reqRsp.getCondition(), response.getCondition());
    assertEquals(reqRsp.getData(), response.getData());
  }
}
