package br.com.litoraltextil.acao;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;

import br.com.litoraltextil.dao.*;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import static br.com.litoraltextil.utils.Utils.inserirDePara;
import static br.com.litoraltextil.utils.Utils.validarOperacao;

public class AlterarDePara implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao ctx) throws Exception {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();

            BigDecimal codUsuario = ctx.getUsuarioLogado();
            String paramCodProd = (String) ctx.getParam("CODPROD");
            String paramNotifica = (String) ctx.getParam("NOTIFICA");
            BigDecimal codProd = new BigDecimal(paramCodProd);

            if (paramCodProd == null) {
                ctx.setMensagemRetorno("Parâmetro <b>Produto</b> é obrigatório.");
                return;
            }

            // Processa registros selecionados
            Registro[] registros = ctx.getLinhas();
            Iterator<Registro> iterator = Arrays.stream(registros).iterator();

            while (iterator.hasNext()) {
                Registro registro = iterator.next();
                BigDecimal nunota = (BigDecimal) registro.getCampo("NUNOTA");
                BigDecimal sequencia = (BigDecimal) registro.getCampo("SEQUENCIA");
                DynamicVO tgfiteVO = ItemNotaDAO.get(nunota, sequencia);
                DynamicVO tgfcabVO = CabecalhoDAO.get(nunota);
                BigDecimal codprod = tgfiteVO.asBigDecimal("CODPROD");
//                String controle = tgfiteVO.asString("CODPROJ");
                DynamicVO produtoAtualVO = ProdutoDAO.get(codprod);
                DynamicVO produtoNovoVO = ProdutoDAO.get(codProd);
                DynamicVO usuarioVO = UsuarioDAO.get(codUsuario);
                String nomeUsuario = codUsuario.toString() + " - " + usuarioVO.asString("NOMEUSU");
                BigDecimal codCorAtual = produtoAtualVO.asBigDecimal("AD_CODCOR");
                BigDecimal codCorNovo = produtoNovoVO.asBigDecimal("AD_CODCOR");
                DynamicVO pantoneAtualVO = ProdutoDAO.getPantone(codCorAtual);
                DynamicVO pantoneNovoVO = ProdutoDAO.getPantone(codCorNovo);
                String corPantoneAtual = pantoneAtualVO.asString("CODCOR2") + " - " + pantoneAtualVO.asString("NOMECOR");
                String corPantoneNovo = pantoneNovoVO.asString("CODCOR2") + " - " + pantoneNovoVO.asString("NOMECOR");


                String mensagemErro = validarOperacao(nunota, codprod, codProd, tgfcabVO.asBigDecimal("CODPROJ"));
                if (mensagemErro != null) {
                    ctx.setMensagemRetorno(mensagemErro);
                    return;
                }

                inserirDePara(tgfcabVO.asBigDecimal("CODPROJ"), codprod, codProd,corPantoneAtual,corPantoneNovo,produtoAtualVO.asString("DESCRPROD"),produtoNovoVO.asString("DESCRPROD"),nomeUsuario,nunota,sequencia,paramNotifica);

                // Monta mensagem de sucesso
                StringBuilder mensagem = new StringBuilder();
                mensagem.append("<b>ITEM ALTERADO [JAVA]</b><br>");
                mensagem.append("De: ").append(codprod.toString()).append(";<br>");
                mensagem.append("Para: ").append(paramCodProd.toString()).append(".<br>");
                mensagem.append("<b>COR ALTERADA</b><br>");
                mensagem.append("De: ").append(corPantoneAtual).append(";<br>");
                mensagem.append("Para: ").append(corPantoneNovo).append(".");

                ctx.setMensagemRetorno(mensagem.toString());
            }

        } catch (Exception e) {
            ctx.setMensagemRetorno("Erro ao processar alteração: " + e.getMessage());
            throw e;
        } finally {
            JapeSession.close(hnd);
        }
    }
}