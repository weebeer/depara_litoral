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

public class NotificaVendedorv2 implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao ctx) throws Exception {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            Integer paramCodProj = (Integer) ctx.getParam("CODPROJ");

            NativeSql sql = new NativeSql(jdbc);
            sql.appendSql("SELECT NUNOTA, DTALTER FROM AD_DEPARAPEDVEN WHERE CODPROJ = :CODPROJ AND NOTIFICADO = 'N'");
            sql.setNamedParameter("CODPROJ", paramCodProj);
            ResultSet rs = sql.executeQuery();

            while (rs.next()) {
                BigDecimal nunota = rs.getBigDecimal("NUNOTA");
                DynamicVO tgfcabVO = CabecalhoDAO.get(nunota);
                DynamicVO tgfparVO = ParceiroDAO.get(tgfcabVO.asBigDecimal("CODPARC"));
                BigDecimal nroForca = tgfcabVO.asBigDecimal("AD_PEDIDOID");
                String razaoSocial = tgfparVO.asString("RAZAOSOCIAL");
//                BigDecimal codvend = tgfcabVO.asBigDecimal("CODVEND");
                String dest = "francisco.silva@litoraltextil.com.br";

                // Buscar informações dos produtos alterados
                StringBuilder tabelaProdutos = new StringBuilder();
                tabelaProdutos.append("<table class=\"products-table\">")
                        .append("<thead>")
                        .append("<tr>")
                        .append("<th>Produto Original</th>")
                        .append("<th>Cor Original</th>")
//                        .append("<th>Produto Novo</th>")
                        .append("<th>Cor Nova</th>")
                        .append("<th>Quantidade (M)</th>")
                        .append("</tr>")
                        .append("</thead>")
                        .append("<tbody>");

                NativeSql sqlProdutos = new NativeSql(jdbc);
                sqlProdutos.appendSql("SELECT \tPRODUTOANT,\n" +
                        "\t\tCODCORANT,\n" +
//                        "\t\tCODHEXANT,\n" +
                        "\t\tCODCORINS,\n" +
//                        "\t\tCODHEXINS,\n" +
                        "\t\tQTDENEG,\n" +
                        "\t\tDTALTER\n" +
                        "FROM AD_DEPARAPEDVEN \n" +
                        "WHERE NUNOTA = :NUNOTA;"
                );
                sqlProdutos.setNamedParameter("NUNOTA", nunota);
                ResultSet rsProd = sqlProdutos.executeQuery();

                while(rsProd.next()) {
                    System.out.println("Processando produto alterado...");
                    String produtoOriginal = rsProd.getString("PRODUTOANT") != null ? rsProd.getString("PRODUTOANT") : "N/A";
//                    String produtoNovo = rsProd.getString("PROD_INS") != null ? rsProd.getString("PROD_INS") : "N/A";
                    String corOriginal = rsProd.getString("CODCORANT") != null ? rsProd.getString("CODCORANT") : "N/A";
//                    String hexOriginal = rsProd.getString("CODHEXANT") != null ? rsProd.getString("CODHEXANT") : "#ffffff";
                    String corNova = rsProd.getString("CODCORINS") != null ? rsProd.getString("CODCORINS") : "N/A";
//                    String hexNova = rsProd.getString("CODHEXINS") != null ? rsProd.getString("CODHEXINS") : "#ffffff";
                    BigDecimal quantidade = rsProd.getBigDecimal("QTDENEG") != null ? rsProd.getBigDecimal("QTDENEG") : BigDecimal.ZERO;

                    tabelaProdutos.append("<tr>")
                            .append("<td class=\"product-name\">").append(produtoOriginal).append("</td>")
                            .append("<td class=\"color-old\">")
                            .append("<div style=\"display: inline-flex; align-items: center;\">")
//                            .append("<div style=\"width: 20px; height: 20px; background-color: ").append(hexOriginal).append("; border: 1px solid #ccc; margin-right: 8px; display: inline-block;\"></div>")
                            .append("<span>").append(corOriginal).append("</span>")
                            .append("</div>")
                            .append("</td>")
//                            .append("<td class=\"product-name\">").append(produtoNovo).append("</td>")
                            .append("<td class=\"color-new\">")
                            .append("<div style=\"display: inline-flex; align-items: center;\">")
//                            .append("<div style=\"width: 20px; height: 20px; background-color: ").append(hexNova).append("; border: 1px solid #ccc; margin-right: 8px; display: inline-block;\"></div>")
                            .append("<span>").append(corNova).append("</span>")
                            .append("</div>")
                            .append("</td>")
                            .append("<td class=\"quantity\">").append(quantidade.toString()).append("</td>")
                            .append("</tr>");
                }

                tabelaProdutos.append("</tbody></table>");

                // Template HTML atualizado com a tabela
                StringBuilder emailHtml = new StringBuilder();
                emailHtml.append("<!DOCTYPE html><html><head><style>")
                        .append("body{font-family: Arial, sans-serif;margin: 0;padding: 20px;background-color: #f5f5f5;}")
                        .append(".container{max-width: 700px;margin: 0 auto;background-color: white;border-radius: 8px;overflow: hidden;box-shadow: 0 2px 10px rgba(0,0,0,0.1);}")
                        .append(".header{padding: 20px;text-align: center;color: white;}")
                        .append(".logo-text{font-size: 28px;font-weight: bold;letter-spacing: 2px;margin: 0;text-shadow: 2px 2px 4px rgba(0,0,0,0.3);}")
                        .append(".tagline{font-size: 12px;margin: 5px 0 0 0;opacity: 0.9;}")
                        .append(".content{padding: 30px;}")
                        .append("h1{color: #1E3A8A;text-align: center;font-size: 20px;margin-bottom: 25px;border-bottom: 2px solid #FF6B35;padding-bottom: 10px;}")
                        .append("p{line-height: 1.6;margin: 15px 0;color: #333;text-align: left;}")
                        .append(".info-box{background-color: #f8f9fa;border-left: 4px solid #FF6B35;padding: 15px;margin: 20px 0;}")
                        .append(".info-box p{margin: 5px 0;}")
                        .append(".products-table{width: 100%;border-collapse: collapse;margin: 20px 0;}")
                        .append(".products-table th{background-color: #1E3A8A;color: white;padding: 12px;text-align: left;font-size: 14px;}")
                        .append(".products-table td{padding: 10px;border-bottom: 1px solid #ddd;font-size: 13px;}")
                        .append(".products-table tr:nth-child(even){background-color: #f8f9fa;}")
                        .append(".product-name{font-weight: bold;color: #1E3A8A;}")
                        .append(".color-old{color: #666;background-color: #ffe6e6;}")
                        .append(".color-new{color: #FF6B35;background-color: #fff2e6;font-weight: bold;}")
                        .append(".quantity{text-align: center;font-weight: bold;}")
                        .append(".contact-box{background: white;color: #1E3A8A;padding: 20px;border-radius: 12px;text-align: center;margin: 20px 0;box-shadow: 0 10px 25px rgba(255, 107, 53, 0.2), 0 5px 10px rgba(30, 58, 138, 0.1);border: 1px solid;border-image: linear-gradient(45deg, #FF6B35, #1E3A8A, #FF6B35) 1;position: relative;overflow: hidden;}")
                        .append(".contact-box::before{content: '';position: absolute;top: 0;left: 0;right: 0;bottom: 0;background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(30, 58, 138, 0.05) 100%);pointer-events: none;}")
                        .append(".contact-box p{margin: 8px 0;}")
                        .append(".footer{background-color: #f8f9fa;padding: 15px;text-align: center;font-size: 11px;color: #666;border-top: 1px solid #eee;}")
                        .append(".highlight{color: #1E3A8A;font-weight: bold;}")
                        .append(".accent{color: #FF6B35;font-weight: bold;}")
                        .append("</style></head><body>")
                        .append("<div class=\"container\">")
                        .append("<div class=\"header\">")
                        .append("<div class=\"logo-container\">")
                        .append("<img src=\"https://static.wixstatic.com/media/75c56f_833850e0756a44ac8088240c9b16c165~mv2.png/v1/fill/w_397,h_149,al_c,q_85,usm_0.66_1.00_0.01,enc_avif,quality_auto/75c56f_833850e0756a44ac8088240c9b16c165~mv2.png\" alt=\"Litoral Têxtil\" class=\"logo-img\">")
                        .append("</div>")
                        .append("</div>")
                        .append("<div class=\"content\">")
                        .append("<h1>Notificação de Alteração de Produto - Pedido Nº ").append(nroForca).append("</h1>")
                        .append("<p>Prezado(a) Parceiro(a) <span class=\"highlight\">").append(razaoSocial).append("</span>,</p>")
                        .append("<p>Informamos que o pedido Nº <span class=\"highlight\">").append(nroForca).append("</span> teve alterações nos produtos conforme detalhado na tabela abaixo:</p>");

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataFormatada = sdf.format(rs.getTimestamp("DTALTER"));

                emailHtml.append("<div class=\"info-box\">")
                        .append("<p><strong>Data da alteração:</strong> ").append(dataFormatada).append("</p>")
                        .append("</div>");

                emailHtml.append(tabelaProdutos.toString());

                emailHtml.append("<p>Solicitamos que verifiquem a disponibilidade dos novos produtos e confirmem o prazo de entrega atualizado.</p>")
                        .append("<div class=\"contact-box\">")
                        .append("<p style=\"text-align: center; font-weight: bold; margin-bottom: 10px;\">Para dúvidas ou confirmações, entre em contato conosco:</p>")
                        .append("<p style=\"margin: 3px 0; font-size: 10px; text-align: center;\"> <strong>Matriz ES:</strong> <a href=\"https://maps.app.goo.gl/WKjeWNRhzczrW3k56\" target=\"_blank\" style=\"color: #0066cc; text-decoration: none;\">Rod. Darli Santos, 5130 - Vila Velha/ES - CEP: 29103-300</a></p>\n")
                        .append("<p style=\"margin: 3px 0; font-size: 10px; text-align: center;\"> <strong>Filial SC:</strong> <a href=\"https://maps.app.goo.gl/WKBizaA4WgDfe9Lr9\" target=\"_blank\" style=\"color: #0066cc; text-decoration: none;\">Rod. BR 280, KM 7,199 - Guaramirim/SC - CEP: 89270-000</a></p>\n")
                        .append("<p style=\"margin: 3px 0; font-size: 10px; text-align: center;\"> <strong>Showroom SP:</strong> <a href=\"https://maps.app.goo.gl/3bKfZUZLx4F7FyhU6\" target=\"_blank\" style=\"color: #0066cc; text-decoration: none;\">Rua Correia de Melo, 49 - Bom Retiro/SP - CEP: 01123-020</a></p>\n").append("<p style=\"text-align: center; margin-top: 12px; font-size: 11px;\"> <strong>+55 (27) 3320-8777</strong> | <strong>vendas@litoraltextil.com.br</strong></p>")
                        .append("</div>")
                        .append("</div>")
                        .append("</body></html>");

                // Criar registro na fila de mensagens
                JapeWrapper tmdfmgDAO = JapeFactory.dao(DynamicEntityNames.FILA_MSG);
                tmdfmgDAO.create()
                        .set("EMAIL", dest)
                        .set("CODCON", BigDecimal.ZERO)
                        .set("CODMSG", null)
                        .set("STATUS", "Pendente")
                        .set("TIPOENVIO", "E")
                        .set("MAXTENTENVIO", BigDecimal.valueOf(3))
                        .set("ASSUNTO", "Notificação de Alteração de Produto - Pedido Nº " + nroForca)
                        .set("MENSAGEM", emailHtml.toString().toCharArray())
                        .set("CODSMTP", BigDecimal.valueOf(10))
                        .save();

                // Marcar como notificado
                NativeSql sqlUpdate = new NativeSql(jdbc);
                sqlUpdate.appendSql("UPDATE AD_DEPARAPEDVEN SET NOTIFICADO = 'S' WHERE NUNOTA = :NUNOTA");
                sqlUpdate.setNamedParameter("NUNOTA", nunota);
                sqlUpdate.executeUpdate();
            }

            ctx.setMensagemRetorno("Notificações processadas com sucesso!");

        } catch (Exception e) {
            ctx.setMensagemRetorno("Erro ao processar alteração: " + e.getMessage());
        } finally {
            JapeSession.close(hnd);
        }
    }
}