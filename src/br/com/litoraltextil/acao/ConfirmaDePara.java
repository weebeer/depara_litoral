package br.com.litoraltextil.acao;

import br.com.litoraltextil.dao.CabecalhoDAO;
import br.com.litoraltextil.dao.NumeracaoDAO;
import br.com.litoraltextil.dao.TabelasAD;
import br.com.litoraltextil.utils.Utils;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static br.com.litoraltextil.utils.Utils.AlteraProdutoPedido;

public class ConfirmaDePara implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao ctx) throws Exception {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            StringBuilder email = new StringBuilder();

            // Processa registros selecionados
            Registro[] registros = ctx.getLinhas();
            Iterator<Registro> iterator = Arrays.stream(registros).iterator();
            BigDecimal id = null;

            while (iterator.hasNext()) {
                Registro registro = iterator.next();
                id = (BigDecimal) registro.getCampo("ID");
                System.out.println("ID do registro: " + id.toString());
            }

            DynamicVO deParaVO = TabelasAD.deParaIdDAO(id);
            BigDecimal codproj = deParaVO.asBigDecimal("CODPROJ");
            String cabecalhoEmail = "<!DOCTYPE html><html>Seguem abaixo as alterações de cores (produtos filhos) no projeto <b>" + codproj.toString() + ":</b></p><table border=\"1px\"><tr style=\"text-align: center;\"><th>Produto Pai</th><th>Produto Substituido</th><th>Produto Substituto</th><th>Cor Substituida</th><th>Cor Substituta</th><th>Status</th></tr>";
            email.append(cabecalhoEmail);
            System.out.println("Cabeçalho do email: " + cabecalhoEmail);
            String usuario = null;

            BigDecimal i = BigDecimal.ONE;
            Collection<DynamicVO> CollectionDeParaVO = TabelasAD.deParaAllidDAO();
            for (DynamicVO DeParaVO : CollectionDeParaVO ){
                BigDecimal codProj = DeParaVO.asBigDecimal("CODPROJ");
                BigDecimal codProdAnt = DeParaVO.asBigDecimal("CODPRODANT");
                BigDecimal codProdIns = DeParaVO.asBigDecimal("CODPRODINS");
                BigDecimal nunota = DeParaVO.asBigDecimal("NUNOTA");
                BigDecimal sequencia = DeParaVO.asBigDecimal("SEQUENCIA");
                String codCorant = DeParaVO.asString("CODCORANT");
                String codCorins = DeParaVO.asString("CODCORINS");
                String produtoAnt = DeParaVO.asString("PRODUTOANT");
                String produtoIns = DeParaVO.asString("PRODUTOINS");
                String notifica = DeParaVO.asString("NOTIFICA");
                Timestamp dtAlter = DeParaVO.asTimestamp("DTALTER");
                usuario = DeParaVO.asString("USUARIO");

                JapeWrapper logDeParaDAO = JapeFactory.dao("AD_LOGDEPARA");
                logDeParaDAO.create()
                        .set("ID", NumeracaoDAO.getLogDeParaID())
                        .set("LOTE", codproj )
                        .set("CODDEPARA", i)
                        .set("CODPROJ", codProj)
                        .set("CODPRODANT", codProdAnt)
                        .set("CODPRODINS", codProdIns)
                        .set("USUARIO", usuario)
                        .set("DTALTER", dtAlter)
                        .set("CODCORANT", codCorant)
                        .set("CODCORINS", codCorins)
                        .set("PRODUTOANT", produtoAnt)
                        .set("PRODUTOINS", produtoIns)
                        .set("NOTIFICA", notifica)
                        .set("DTENVIO", new Timestamp(System.currentTimeMillis()))
                        .set("USUARIOENVIO", usuario)
                        .save();

                JapeWrapper tgfIteVO = JapeFactory.dao("ItemNota");
                tgfIteVO.prepareToUpdateByPK(nunota, sequencia)
                        .set("CODPROD", codProdIns)
                        .update();

                JapeWrapper sortProdVO = JapeFactory.dao("AD_SORTPROD");
                DynamicVO sortProdVOO = sortProdVO.findOne("CODPROJ = ? AND CODPRODSON = ?", codProj, codProdAnt);
                sortProdVO.prepareToUpdate(sortProdVOO)
                        .set("CODPRODSON", codProdIns)
                        .update();

                JapeWrapper ordDeParaVO = JapeFactory.dao("AD_ORDDEPARA");
                ordDeParaVO.create()
                        .set("ID", NumeracaoDAO.getOrdDeParaID())
                        .set("PRODUTOANT", produtoAnt)
                        .set("CODPRODANT", codProdAnt)
                        .set("CODCORANT", codCorant)
                        .set("CODPRODINS", codProdIns)
                        .set("CODCORINS", codCorins)
                        .save();

                i = i.add(BigDecimal.ONE);
            }

            JapeWrapper logDeParaDAO = JapeFactory.dao("AD_LOGDEPARA");
            DynamicVO logDeParaVO = logDeParaDAO.findOne("CODPROJ = ?", codproj);
            logDeParaDAO.prepareToUpdate(logDeParaVO)
                    .set("DTENVIO", new Timestamp(System.currentTimeMillis()))
                    .set("USUARIOENVIO", usuario)
                    .update();

            String status = null;
            DynamicVO TipoPedidoCabVO = CabecalhoDAO.getOneProjeto(codproj);
            if (TipoPedidoCabVO != null) {
                status = "PRONTA ENTREGA";
            } else {
                status = "PROGRAMADO";
            }

            Collection<DynamicVO> CollectionDeParaVO2 = TabelasAD.deParaAllCodProjDAO(codproj);
            for (DynamicVO DeParaVO2 : CollectionDeParaVO2 ){
                String produtoAnt2 = DeParaVO2.asString("PRODUTOANT");
                BigDecimal codProdAnt2 = DeParaVO2.asBigDecimal("CODPRODANT");
                String codCorAnt2 = DeParaVO2.asString("CODCORANT");
                BigDecimal codProdIns2 = DeParaVO2.asBigDecimal("CODPRODINS");
                String codCorIns2 = DeParaVO2.asString("CODCORINS");

                email   .append("<tr style=\"text-align: center;\"><td>").append(produtoAnt2).append("</td>")
                        .append("<td>").append(codProdAnt2).append("</td>")
                        .append("<td>").append(codProdIns2).append("</td>")
                        .append("<td>").append(codCorAnt2).append("</td>")
                        .append("<td>").append(codCorIns2).append("</td>")
                        .append("<td>").append(status).append("</td></tr>");

                JapeWrapper ordDeParaVO2 = JapeFactory.dao("AD_ORDDEPARA");
                ordDeParaVO2.deleteByCriteria("CODPRODANT = ?", codProdAnt2);

                AlteraProdutoPedido(DeParaVO2);
            }

            JapeWrapper deParaDAO = JapeFactory.dao("AD_DEPARA");
            deParaDAO.deleteByCriteria("CODPROJ = ?", codproj);

            Utils.enviarEmailDePara(email,usuario,codproj);

        } catch (Exception e) {
            ctx.setMensagemRetorno("Erro ao processar alteração: " + e.getMessage());
            throw e;
        } finally {
            JapeSession.close(hnd);
        }
    }
}



