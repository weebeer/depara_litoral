package br.com.litoraltextil.utils;

import br.com.litoraltextil.dao.CabecalhoDAO;
import br.com.litoraltextil.dao.ParceiroDAO;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Utils {

    public static void AlteraProdutoPedido(DynamicVO logDePara, BigDecimal codusu) throws Exception {
        JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
        try {
            BigDecimal id = logDePara.asBigDecimal("ID");
            BigDecimal codprodant = logDePara.asBigDecimal("CODPRODANT");
            BigDecimal codprodins = logDePara.asBigDecimal("CODPRODINS");
            BigDecimal codproj = logDePara.asBigDecimal("CODPROJ");
            String corAnterior = logDePara.asString("CODCORANT");
            String corInserida = logDePara.asString("CODCORINS");
            String nomeProduto = logDePara.asString("PRODUTOANT");
            Timestamp dataAlteracao = logDePara.asTimestamp("DTALTER");
            NativeSql sql = new NativeSql(jdbc);
            sql.appendSql("SELECT NUNOTA , SEQUENCIA FROM TGFITE WHERE  CODPROD = :CODPROD AND CONTROLE = :CONTROLE");
            sql.setNamedParameter("CODPROD", codprodant);
            sql.setNamedParameter("CONTROLE",codproj.toString());
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                BigDecimal nunota = rs.getBigDecimal("NUNOTA");
                BigDecimal sequencia = rs.getBigDecimal("SEQUENCIA");

                JapeWrapper tgfite = JapeFactory.dao("ItemNota");
                tgfite.prepareToUpdateByPK(nunota, sequencia)
                        .set("CODPROD", codprodins)
                        .update();

                JapeWrapper logDeParaPedidoVenda = JapeFactory.dao("AD_DEPARAPEDVEN");
                logDeParaPedidoVenda.create()
                        .set("NUNOTA", nunota)
                        .set("SEQITE", sequencia)
                        .set("CODPRODANT", codprodant)
                        .set("CODPRODINS", codprodins)
                        .set("CODPROJ", codproj)
                        .set("DTALTER", new Timestamp(System.currentTimeMillis()))
                        .set("CODUSU", codusu)
                        .save();

                if (logDePara.asString("NOTIFICAPARC").compareTo("S") == 0) {
                    NotificaParceiro(nunota,corAnterior,corInserida,nomeProduto,dataAlteracao);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jdbc.closeSession();
        }
    }

    public static void NotificaParceiro(BigDecimal nunota , String corAnt , String corIns , String prodAnt , Timestamp dtAlter) throws Exception {
        try {
            System.out.println("Iniciando notificação ao parceiro...");

            DynamicVO tgfcabVO = CabecalhoDAO.get(nunota);
            DynamicVO tgfparVO = ParceiroDAO.get(tgfcabVO.asBigDecimal("CODPARC"));
            BigDecimal nroForca = tgfcabVO.asBigDecimal("AD_PEDIDOID");
            String razaoSocial = tgfparVO.asString("RAZAOSOCIAL");
            BigDecimal codContato = tgfcabVO.asBigDecimal("CODCONTATO");

                /*EMAIL PARA O VENDEDOR */
            StringBuilder emailConcatenado = new StringBuilder();
            emailConcatenado.append(tgfparVO.asString("EMAIL").trim());
            if (codContato != null && codContato.compareTo(BigDecimal.ZERO) > 0) {
                DynamicVO contatoVO = JapeFactory.dao("Contato").findByPK(codContato);
                if (!contatoVO.asString("EMAIL").trim().isEmpty()) {
                    emailConcatenado.append(",");
                    emailConcatenado.append(contatoVO.asString("EMAIL").trim());
                }
            }

            String msgTratada = "<!DOCTYPE html><html><head><style>body{font-family: Arial, sans-serif;margin: 0;padding: 20px;background-color: #f5f5f5;}.container{max-width: 600px;margin: 0 auto;background-color: white;border-radius: 8px;overflow: hidden;box-shadow: 0 2px 10px rgba(0,0,0,0.1);}.header{padding: 20px;text-align: center;color: white;}.logo-text{font-size: 28px;font-weight: bold;letter-spacing: 2px;margin: 0;text-shadow: 2px 2px 4px rgba(0,0,0,0.3);}.tagline{font-size: 12px;margin: 5px 0 0 0;opacity: 0.9;}.content{padding: 30px;}h1{color: #1E3A8A;text-align: center;font-size: 20px;margin-bottom: 25px;border-bottom: 2px solid #FF6B35;padding-bottom: 10px;}p{line-height: 1.6;margin: 15px 0;color: #333;text-align: left;}.info-box{background-color: #f8f9fa;border-left: 4px solid #FF6B35;padding: 15px;margin: 20px 0;}.info-box p{margin: 5px 0;}.contact-box{background: white;color: #1E3A8A;padding: 20px;border-radius: 12px;text-align: center;margin: 20px 0;box-shadow: 0 10px 25px rgba(255, 107, 53, 0.2), 0 5px 10px rgba(30, 58, 138, 0.1);border: 1px solid;border-image: linear-gradient(45deg, #FF6B35, #1E3A8A, #FF6B35) 1;position: relative;overflow: hidden;}.contact-box::before{content: '';position: absolute;top: 0;left: 0;right: 0;bottom: 0;background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(30, 58, 138, 0.05) 100%);pointer-events: none;}.contact-box p{margin: 8px 0;}.footer{background-color: #f8f9fa;padding: 15px;text-align: center;font-size: 11px;color: #666;border-top: 1px solid #eee;}.highlight{color: #1E3A8A;font-weight: bold;}.accent{color: #FF6B35;font-weight: bold;}</style></head><body><div class=\\\"container\\\"><div class=\\\"header\\\"><div class=\\\"logo-container\\\"><img src=\\\"https://static.wixstatic.com/media/75c56f_833850e0756a44ac8088240c9b16c165~mv2.png/v1/fill/w_397,h_149,al_c,q_85,usm_0.66_1.00_0.01,enc_avif,quality_auto/75c56f_833850e0756a44ac8088240c9b16c165~mv2.png\\\" alt=\\\"Litoral Têxtil\\\" class=\\\"logo-img\\\"></div></div><div class=\\\"content\\\"><h1>Notificação de Alteração de Produto - Pedido Nº [nroForca]</h1><p>Prezado(a) Parceiro(a) <span class=\\\"highlight\\\">[razaoSocial]</span>,</p><p>Informamos que o pedido Nº <span class=\\\"highlight\\\">[nroForca]</span> teve alteração na especificação do produto <span class=\\\"accent\\\">[nomeProduto]</span>.</p><div class=\\\"info-box\\\"><p><strong>Alteração realizada:</strong> Troca de cor do tecido</p><p><strong>Cor anterior:</strong> <span class=\\\"highlight\\\">[corAnterior]</span></p><p><strong>Nova cor:</strong> <span class=\\\"accent\\\">[corInserida]</span></p><p><strong>Data da alteração:</strong> [dataAlteracao]</p></div><p>Solicitamos que verifiquem a disponibilidade do novo produto e confirmem o prazo de entrega atualizado.</p><div class=\\\"contact-box\\\"><p><strong>Para dúvidas ou confirmações:</strong></p><p>📞 Telefone: (27) 3333-4444</p><p>📧 Email: vendas@litoraltextil.com.br</p></div></div><div class=\\\"footer\\\"><p>Notificação enviada automaticamente pelo sistema Litoral Têxtil.<br>Em caso de problemas técnicos, contatar o setor de TI.</p></div></div></body></html>";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(dtAlter);

            msgTratada = msgTratada.replace("[nroForca]", nroForca.toString())
                                    .replace("[razaoSocial]", razaoSocial)
                                    .replace("[corAnterior]", corAnt)
                                    .replace("[corInserida]", corIns)
                                    .replace("[nomeProduto]", prodAnt)
                                    .replace("[dataAlteracao]", dataFormatada);
            JapeWrapper tmdfmgDAO = JapeFactory.dao(DynamicEntityNames.FILA_MSG);
            tmdfmgDAO.create()
                    .set("EMAIL",emailConcatenado)
                    .set("CODCON", BigDecimal.ZERO)
                    .set("CODMSG", null)
                    .set("STATUS", "Pendente")
                    .set("TIPOENVIO", "E")
                    .set("MAXTENTENVIO", BigDecimal.valueOf(3))
                    .set("ASSUNTO", "Notificação de Alteração de Produto - Pedido Nº " + nroForca)
                    .set("MENSAGEM", msgTratada.toCharArray())
                    .set("CODSMTP", BigDecimal.valueOf(10))
                    .save();

            System.out.println("Notificação enviada para o parceiro.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro ao notificar o parceiro: " + e.getMessage());
        }
    }

}
