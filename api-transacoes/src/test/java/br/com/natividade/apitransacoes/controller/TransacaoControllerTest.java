package br.com.natividade.apitransacoes.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.natividade.apitransacoes.dataprovider.TransacaoDataProvider;
import br.com.natividade.apitransacoes.entrypoint.controller.form.TransacaoForm;
import br.com.natividade.apitransacoes.model.TransacaoModel;

@SpringBootTest
@AutoConfigureMockMvc
public class TransacaoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TransacaoDataProvider transacaoDataProvider;

	@Test
	public void deveRealizarTransacao() throws JsonProcessingException, Exception {
		// cenário
		TransacaoForm transacaoForm = new TransacaoForm();
		transacaoForm.setDataHora(LocalDateTime.now());
		transacaoForm.setValor(new BigDecimal(2100));

		// ação
		mockMvc.perform(MockMvcRequestBuilders.post("/transacao").contentType("application/json")
				.content(objectMapper.writeValueAsString(transacaoForm)))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		List<TransacaoModel> transacoes = transacaoDataProvider.getTransacoes();

		// validação
		Assertions.assertEquals(1, transacoes.size());
		Assertions.assertEquals(transacaoForm.getValor(), transacoes.get(0).getValor());
		Assertions.assertEquals(transacaoForm.getDataHora(), transacoes.get(0).getDataHora());
	}
	
	@Test
	public void deveDeletarTransacoes() throws JsonProcessingException, Exception {
		// cenário
		TransacaoForm transacaoForm = new TransacaoForm();
		transacaoForm.setDataHora(LocalDateTime.now());
		transacaoForm.setValor(new BigDecimal(2500));

		transacaoDataProvider.realizarTransacao(new TransacaoModel(transacaoForm.getDataHora(), transacaoForm.getValor()));
		
		// ação
		this.mockMvc.perform(MockMvcRequestBuilders
	            .delete("/transacao")
	            .accept("*/*"))
	            .andExpect(MockMvcResultMatchers.status().isOk());
	
		List<TransacaoModel> transacoes = transacaoDataProvider.getTransacoes();

		// validação
		Assertions.assertEquals(0, transacoes.size());
	}

}
