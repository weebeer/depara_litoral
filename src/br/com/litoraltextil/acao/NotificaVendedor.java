package br.com.litoraltextil.acao;

import br.com.litoraltextil.dao.CabecalhoDAO;
import br.com.litoraltextil.dao.ParceiroDAO;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class NotificaVendedor implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao ctx) throws Exception {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            BigDecimal paramCodProj = (BigDecimal) ctx.getParam("CODPROJ");
            StringBuilder email = new StringBuilder();

            NativeSql sql = new NativeSql(jdbc);
            sql.appendSql("SELECT NUNOTA FROM AD_DEPARAPEDVEN WHERE CODPROJ = :CODPROJ AND NOTIFICA = 'N'");
            sql.setNamedParameter("CODPROJ", paramCodProj);
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                BigDecimal nunota = rs.getBigDecimal("NUNOTA");
                DynamicVO tgfcabVO = CabecalhoDAO.get(nunota);
                DynamicVO tgfparVO = ParceiroDAO.get(tgfcabVO.asBigDecimal("CODPARC"));
                BigDecimal nroForca = tgfcabVO.asBigDecimal("AD_PEDIDOID");
                String razaoSocial = tgfparVO.asString("RAZAOSOCIAL");
                BigDecimal codvend = tgfcabVO.asBigDecimal("CODVEND");
                String dest = "francisco.silva@litoraltextil.com.br";

////            if (codvend != null && codvend.compareTo(BigDecimal.ZERO) > 0) {
////                DynamicVO vendedorVO = JapeFactory.dao("Vendedor").findByPK(codvend);
////                if (!vendedorVO.asString("EMAIL").trim().isEmpty()) {
////                    emailConcatenado.append(",");
////                    emailConcatenado.append(vendedorVO.asString("EMAIL").trim());
////                }
////            }

                email.append("<!DOCTYPE html><html><head><style>body{font-family: Arial, sans-serif;margin: 0;padding: 20px;background-color: #f5f5f5;}.container{max-width: 600px;margin: 0 auto;background-color: white;border-radius: 8px;overflow: hidden;box-shadow: 0 2px 10px rgba(0,0,0,0.1);}.header{padding: 20px;text-align: center;color: white;}.logo-text{font-size: 28px;font-weight: bold;letter-spacing: 2px;margin: 0;text-shadow: 2px 2px 4px rgba(0,0,0,0.3);}.tagline{font-size: 12px;margin: 5px 0 0 0;opacity: 0.9;}.content{padding: 30px;}h1{color: #1E3A8A;text-align: center;font-size: 20px;margin-bottom: 25px;border-bottom: 2px solid #FF6B35;padding-bottom: 10px;}p{line-height: 1.6;margin: 15px 0;color: #333;text-align: left;}.info-box{background-color: #f8f9fa;border-left: 4px solid #FF6B35;padding: 15px;margin: 20px 0;}.info-box p{margin: 5px 0;}.contact-box{background: white;color: #1E3A8A;padding: 20px;border-radius: 12px;text-align: center;margin: 20px 0;box-shadow: 0 10px 25px rgba(255, 107, 53, 0.2), 0 5px 10px rgba(30, 58, 138, 0.1);border: 1px solid;border-image: linear-gradient(45deg, #FF6B35, #1E3A8A, #FF6B35) 1;position: relative;overflow: hidden;}.contact-box::before{content: '';position: absolute;top: 0;left: 0;right: 0;bottom: 0;background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(30, 58, 138, 0.05) 100%);pointer-events: none;}.contact-box p{margin: 8px 0;}.footer{background-color: #f8f9fa;padding: 15px;text-align: center;font-size: 11px;color: #666;border-top: 1px solid #eee;}.highlight{color: #1E3A8A;font-weight: bold;}.accent{color: #FF6B35;font-weight: bold;}</style></head><body><div class=\\\"container\\\"><div class=\\\"header\\\"><div class=\\\"logo-container\\\"><img src=\\\"https://static.wixstatic.com/media/75c56f_833850e0756a44ac8088240c9b16c165~mv2.png/v1/fill/w_397,h_149,al_c,q_85,usm_0.66_1.00_0.01,enc_avif,quality_auto/75c56f_833850e0756a44ac8088240c9b16c165~mv2.png\\\" alt=\\\"Litoral Têxtil\\\" class=\\\"logo-img\\\"></div></div><div class=\\\"content\\\"><h1>Notificação de Alteração de Produto - Pedido Nº [nroForca]</h1><p>Prezado(a) Parceiro(a) <span class=\\\"highlight\\\">[razaoSocial]</span>,</p><p>Informamos que o pedido Nº <span class=\\\"highlight\\\">[nroForca]</span> teve alteração na especificação do produto <span class=\\\"accent\\\">[nomeProduto]</span>.</p><div class=\\\"info-box\\\"><p><strong>Alteração realizada:</strong> Troca de cor do tecido</p><p><strong>Cor anterior:</strong> <span class=\\\"highlight\\\">[corAnterior]</span></p><p><strong>Nova cor:</strong> <span class=\\\"accent\\\">[corInserida]</span></p><p><strong>Data da alteração:</strong> [dataAlteracao]</p></div><p>Solicitamos que verifiquem a disponibilidade do novo produto e confirmem o prazo de entrega atualizado.</p><div class=\\\"contact-box\\\"><p><strong>Para dúvidas ou confirmações:</strong></p><p>📞 Telefone: (27) 3333-4444</p><p>📧 Email: vendas@litoraltextil.com.br</p></div></div><div class=\\\"footer\\\"><p>Notificação enviada automaticamente pelo sistema Litoral Têxtil.<br>Em caso de problemas técnicos, contatar o setor de TI.</p></div></div></body></html>");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataFormatada = sdf.format(rs.getTimestamp("DTALTER"));

//                msgTratada = msgTratada.replace("[nroForca]", nroForca.toString())
//                        .replace("[razaoSocial]", razaoSocial)
//                        .replace("[corAnterior]", corAnt)
//                        .replace("[corInserida]", corIns)
//                        .replace("[nomeProduto]", prodAnt)
//                        .replace("[dataAlteracao]", dataFormatada);

                NativeSql sqlProdutos = new NativeSql(jdbc);
                sqlProdutos.appendSql("SELECT DISTINCT CODPRODANT,CODPRODINS FROM AD_DEPARAPEDVEN WHERE NUNOTA = :NUNOTA");
                sqlProdutos.setNamedParameter("NUNOTA", nunota);
                ResultSet rsProd = sqlProdutos.executeQuery();
                while(rsProd.next()){

                    BigDecimal codProdAnt = rsProd.getBigDecimal("CODPRODANT");
                    BigDecimal codProdIns = rsProd.getBigDecimal("CODPRODINS");

                    email.append("<tr><td class=\"product-name\">").append(codProdAnt).append("</td>");
                    email.append("<td class=\"color-old\">").append("COR_ORIGINAL").append("</td>");
                    email.append("<td class=\"color-new\">").append("NOVA_COR").append("</td>");
                    email.append("<td>").append("QUANTIDADE").append("</td></tr>");

                }

                JapeWrapper tmdfmgDAO = JapeFactory.dao(DynamicEntityNames.FILA_MSG);
                tmdfmgDAO.create()
                        .set("EMAIL",dest)
                        .set("CODCON", BigDecimal.ZERO)
                        .set("CODMSG", null)
                        .set("STATUS", "Pendente")
                        .set("TIPOENVIO", "E")
                        .set("MAXTENTENVIO", BigDecimal.valueOf(3))
                        .set("ASSUNTO", "Notificação de Alteração de Produto - Pedido Nº " + nroForca)
                        .set("MENSAGEM", email.toString().toCharArray())
                        .set("CODSMTP", BigDecimal.valueOf(10))
                        .save();
            }
        } catch (Exception e) {
            ctx.setMensagemRetorno("Erro ao processar alteração: " + e.getMessage());
            throw e;
        } finally {
            JapeSession.close(hnd);
        }
    }
}
