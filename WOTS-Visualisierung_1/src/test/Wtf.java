package test;

import wots.WinternitzOTS;
import files.AESPRF;
import files.PseudorandomFunction;
import files.ByteUtils;
import java.security.SecureRandom;
import java.util.Random;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Moritz Horsch <horsch@cdc.informatik.tu-darmstadt.de>
 */
public class Wtf {

    private static final Logger logger = LoggerFactory.getLogger(WinternitzOTS.class);
    private static WinternitzOTS instance;
    private static byte[] signature, message, seed;

    @BeforeClass
    public static void setUpClass() throws Exception {
	try {
	    instance = new WinternitzOTS(4);

	    PseudorandomFunction prf = new AESPRF.AES128();
//	    PseudorandomFunction prf = new SHAPRF.SHA256();
	    int n = prf.getLength();

	    SecureRandom sRandom = new SecureRandom();
	    seed = new byte[n];
//	    seed[n] = 0x01;
	    sRandom.nextBytes(seed);

	    byte[] x = new byte[n];
//	    x[n] = 0x01;
	    sRandom.nextBytes(x);
	    instance.init(prf, x);
	} catch (Exception e) {
	    logger.error("Exception", e);
	    Assert.fail(e.getMessage());
	}
    }

    @Test
    public void testGenerateKeyPair() {
	try {
	    long t = System.currentTimeMillis();

	    instance.generatePrivateKey(seed);
	    logger.debug("Private key generated in {} msec", System.currentTimeMillis() - t);

	    t = System.currentTimeMillis();
	    instance.generatePublicKey();
	    logger.debug("Public key generated in {} msec", System.currentTimeMillis() - t);

	    logger.debug("Key size is {} ", ByteUtils.convert(instance.getPrivateKey()).length);
	} catch (Exception e) {
	    logger.error("Exception", e);
	    Assert.fail(e.getMessage());
	}
    }

    @Test(dependsOnMethods = {"testGenerateKeyPair"})
    public void testSignAndVerify() {
	try {
	    Random r = new Random();
	    message = new byte[8];
	    r.nextBytes(message);

	    signature = instance.sign(message);
	    logger.trace("Signature: {} ({} bytes)", new Object[]{ByteUtils.toHexString(signature), signature.length});

	    boolean valid = instance.verify(message, signature);
	    Assert.assertTrue(valid);
	} catch (Exception e) {
	    logger.error("Exception", e);
	    Assert.fail(e.getMessage());
	}
    }

    @Test(dependsOnMethods = {"testSignAndVerify"})
    public void testGeneratePublicKey() {
	try {
	    byte[][] pk1 = instance.getPublicKey();
	    byte[][] pk2 = instance.generatePublicKey(message, signature);

	    Assert.assertEquals(pk1.length, pk2.length);
	    for (int i = 0; i < pk1.length; i++) {
		Assert.assertEquals(pk1[i], pk2[i]);
	    }
	} catch (Exception e) {
	    logger.error("Exception", e);
	    Assert.fail(e.getMessage());
	}
    }

    @Test(dependsOnMethods = {"testSignAndVerify"}, enabled=!true)
    public void testGeneratePublicKey2() {
	try {
	    byte[] signature = Hex.decode("91480AD5BDD0B628898E443D7123A21CAE1A163D90DF9D8B8CA8060140A7DB672B1216F2E601EF28414110E19793DC5885B271E0311867EB7A0C9DFCB175738FFE649437E35BAEF6F8A0C65FC5487792596DB841F77F942A90226A5CCC6C5EDD8628FCDAD3378DE9A1E3D9182BCFE50C3F804D4427F373B048555504A00D798DE095D9C44B59478E84C95C63AD7E8024382E7B45496048C82724A37244507C9920A249058021EC8C0F7EC7D8EE83EB81B70C8DF4ACAF989D6EC1D146E85DB81957302A326B8D6C40C6617711A2B9B07DD95C6CFFB73AC83AB35C920622DB73FDD1E96AC19DCB08452CAD20FE4D9B072A3CD0AF20E9917E641B204F3CC552DF5DBC68279ED294D7E1EF177AAD01B5E5EF74C27778A1CAF32AC1607813CBC47906DB35BA6606861E740E38944F5560D91DE7C1DE03C3F40581B307B92C3B55713B08C7C4D6B1C1CC2B35C8880B59C716D87071BC05C858EFCCA9209D74D8C72DEEE90606B0DDA4FFD2266B587FB3A1C38CEDD4C0AE4017DA0F02776E56637BF674AAAB6E0FBEC39C496FF729B16CF497DA903062DFEDBE74BDD04D0092E5136407DA630C30D77800BAFC1AD21EE8F7D82742F6F201CB186470AB51651863EB802E7099C504AE7A210A89FE1CA982CC54B8AB1CB89ECCA0203E261F66E2636EA163B6765632DAE3BE235E1B1CBB3AD5DB257FD97AE27DB98ED74DEEA1E775DEB0B087178A4FDB2B7394FA305ECEAFBA3DC3B132183E965BD9B6C7A52BC158E7EAC0EFF150D2A14122D7CCC6EA2B5FAC145C2872796E8F7FBB2A1939FB4C94677BA2AD2F599068BFDAE7EB31A727707CECF13783C822545F8D1D146AE00EC62E12059324FB7E16CF4B5C2704655D630450B5B8830AB7775D81218A772F4649ED7E124125CA5183126A72F4C5F0DB5B509EBD0DD17E335830672C10250BBAF4A0EF1CE02E37E0C3889A638DB9532CB1E0440ECD911B1F612C880FC55CB6B3D77E3C29BBC7F0CB52C2765B44D360DD49DD4B45C71CF8BE8BE31171E39978CF81B3ACEC2E37B2E59FDB310F473147FE7E98878B06DA232AD518C7C7C29DD381B53AA9A64CB593F14023D12D0DDD5F9C2087B8BF3ACAC095D231F03670280E4090535868E477D2328DEDEC65BF821C461EA12934DF09435C5649F78D25A4D886F0DF238842168F7B8BED216FE6E86B7FBFC8ABA32995893F582389098CFBFDC363F99C0B5E03D05B8DAFA03252952B57BCE9196F859E37E2BC0B7AB22EE2F391407AFD77027B1CCCE17090AF0B6D7541DD6F83478C6113428A1B5D7B6C13B7CFB1D54C9D652F51F0F57CC77197AEC1226E33715001A7E13A98927DE4DDCF657B8FB411D9403C0111825AF36BFE16190CDE964FD939BFE560D589E5B13B53C6D689DF8FF5C0DD84533F742D0132C19E57C90C5053517283D8BDB1B6E7A9B979E5010337F9D2F83670492BC0A65C6BA9C47FE7093A09C7B53E1660D2029E1630F40E37B00190F6A84F76FDADFFBAE04F470FAE5E73C9D48F53FE1F1E574C6356716E4C19D32D41774FB203DE4FD3D070A13C705BCF9C8028A201DC0407DBC0E08A7BE9B0CB26C8D0C2410224FECF6B9CCBD68E4DA7508AFE11EDE997A66E6EB5FEF78CE8765E9D8D2182970A5020411D8C7D8642EA5BC311996A0ED2C9E7E8D91EA87ED8FED50CDD7A5EA7B68E723756AB3F32D0276B7EEC3085C104AB1F721805EE5E57BAA9BC3BEA4F7782EDCCA2BA4FAA5A372C849D516DBAC0777442BFB51670E5535C7986BD1F55D3902C93C5741C0BB4B00F3C8D7C736E994C838D2F5A847512005E12059AACEE4CA25350D520B6D4E101530AADBC0161E2EC4DB17D7CC0FA43A417275224E1AAD801DCC309D11CCA0BADA0ACDA2CD524CA5F968058A45AD64FC4DB7725B7A171CC831B5C3C65835796EBDFFCAF8885809CCD23ABCE1D08226D52EEBE4169F7594981BD1A3D0DDB792D52415372979CA2F7F3412E1DBFE9FDFCDAE410A5EE5AC4C47C3D7425450E61407E51C10882E31F48A10460581790436E7A0B04C3D7ADA3501702545C3C9BF63FC85CA65DD3152D98060E53492359678633C79FCEF73DAE1883DC5E823BB4191A362E7CF47D9F4D28AD946B413A37354A12CCCCAB33CAB8997ED4FCDD40F7C0F03F9F7AF7631A9C9156D774F6B109307FC42B36D1FD73FA183544A43C88D38F97E6FA161E9EF138E3C4B0A260C8F93685C75C358C4EAE328AF2CF5BBC6DB9D698C87A4BA921F2615980D6A4026512AAC547BF2EF326463B2B735CA541932B9D3B8CA5292C7E3C850C58FF8537CFCC11DCFA14D4F963B0B8CDDEB5E3C4E4CA61E656AA4F21F1673EEE79BE8687A5779B4AB5931FB440DE2885A390F9F8C3799FA1ACE15A9B2531FF3ABAECCF1801C225070E84DE9B005AE89A236464F3F0459949630A9A210CA809DC883AD19D43B578F5685368B6FEB396E0821E5E5DC6B3FE9855460AEC802C186F22CE124CB6750F3F3B79FDB2A3A403BF508B5B914399E3B1CB1610959253DDFAE5E5984BDCECC7DC7C35A6AC4878A3A9F67E2AF34C4748C9B1991235F51608336EBAABAB3F82C0E735D7EB8365004C1B42CF90C6C34424EDFD8C6474A6095B8024E4FC35465CEF966417D0CAA6F6131256F06C1C5F1F965F25017DB9C2A9EE31EB40FBEFDC8E186D2DE57F4AD2BF0F1700F8883174651AC894F5CECF53361E15E9724B792D5001E539A5C02A789C6F21B71E12942E0D756CB9E63260592627AE0393A9F7C8C883B8B5359BA2D7553BA9C424DDBFE4FF9B803093EEF0FA0F8D95ADE895C8D6B1B545665A571F58CF77609B26031E173751FE003E5E11559B3E12A321B93CC7EA01FE7268AB97F49B7BC6721BC5532206B441D9ECF3A262B834897DD00B24EE25E66921A1E5495B4C6ABEBBC5D33BD06FFE4DF63C9CDC949BBC63D94F39D63BE20329A17D9C94E01CA920E7B4DF51F4EA690FF6326417F8B238042612AA1FC81CCFA2852CBF121BBF84EFCB1F85CC7EC0A5CACA88");
	    byte[] message = Hex.decode("3021300906052B0E03021A050004148D9643962CB7F4094F750B39CA88F4331615AD7E");
	    byte[][] pk1 = instance.generatePublicKey(message, signature);
	    signature = Hex.decode("91480AD5BDD0B628898E443D7123A21CAE1A163D90DF9D8B8CA8060140A7DB672B1216F2E601EF28414110E19793DC5885B271E0311867EB7A0C9DFCB175738FFE649437E35BAEF6F8A0C65FC5487792596DB841F77F942A90226A5CCC6C5EDD8628FCDAD3378DE9A1E3D9182BCFE50C3F804D4427F373B048555504A00D798DE095D9C44B59478E84C95C63AD7E8024382E7B45496048C82724A37244507C9920A249058021EC8C0F7EC7D8EE83EB81B70C8DF4ACAF989D6EC1D146E85DB81957302A326B8D6C40C6617711A2B9B07DD95C6CFFB73AC83AB35C920622DB73FDD1E96AC19DCB08452CAD20FE4D9B072A3CD0AF20E9917E641B204F3CC552DF5DBC68279ED294D7E1EF177AAD01B5E5EF74C27778A1CAF32AC1607813CBC47906DB35BA6606861E740E38944F5560D91DE7C1DE03C3F40581B307B92C3B55713B08C7C4D6B1C1CC2B35C8880B59C716D87071BC05C858EFCCA9209D74D8C72DEEE90606B0DDA4FFD2266B587FB3A1C38CEDD4C0AE4017DA0F02776E56637BF674AAAB6E0FBEC39C496FF729B16CF497DA903062DFEDBE74BDD04D0092E5136407DA630C30D77800BAFC1AD21EE8F7D82742F6F201CB186470AB51651863EB802E7099C504AE7A210A89FE1CA982CC54B8AB1CB89ECCA0203E261F66E2636EA163B6765632DAE3BE235E1B1CBB3AD5DB257FD97AE27DB98ED74DEEA1E775DEB0B087178A4FDB2B7394FA305ECEAFBA3DC3B132183E965BD9B6C7A52BC158E7EAC0EFF150D2A14122D7CCC6EA2B5FAC145C2872796E8F7FBB2A1939FB4C94677BA2AD2F599068BFDAE7EB31A727707CECF13783C822545F8D1D146AE00EC62E12059324FB7E16CF4B5C2704655D630450B5B8830AB7775D81218A772F4649ED7E124125CA5183126A72F4C5F0DB5B509EBD0DD17E335830672C10250BBAF4A0EF1CE02E37E0C3889A638DB9532CB1E0440ECD911B1F612C880FC55CB6B3D77E3C29BBC7F0CB52C2765B44D360DD49DD4B45C71CF8BE8BE31171E39978CF81B3ACEC2E37B2E59FDB310F473147FE7E98878B06DA232AD518C7C7C29DD381B53AA9A64CB593F14023D12D0DDD5F9C2087B8BF3ACAC095D231F03670280E4090535868E477D2328DEDEC65BF821C461EA12934DF09435C5649F78D25A4D886F0DF238842168F7B8BED216FE6E86B7FBFC8ABA32995893F582389098CFBFDC363F99C0B5E03D05B8DAFA03252952B57BCE9196F859E37E2BC0B7AB22EE2F391407AFD77027B1CCCE17090AF0B6D7541DD6F83478C6113428A1B5D7B6C13B7CFB1D54C9D652F51F0F57CC77197AEC1226E33715001A7E13A98927DE4DDCF657B8FB411D9403C0111825AF36BFE16190CDE964FD939BFE560D589E5B13B53C6D689DF8FF5C0DD84533F742D0132C19E57C90C5053517283D8BDB1B6E7A9B979E5010337F9D2F83670492BC0A65C6BA9C47FE7093A09C7B53E1660D2029E1630F40E37B00190F6A84F76FDADFFBAE04F470FAE5E73C9D48F53FE1F1E574C6356716E4C19D32D41774FB203DE4FD3D070A13C705BCF9C8028A201DC0407DBC0E08A7BE9B0CB26C8D0C2410224FECF6B9CCBD68E4DA7508AFE11EDE997A66E6EB5FEF78CE8765E9D8D2182970A5020411D8C7D8642EA5BC311996A0ED2C9E7E8D91EA87ED8FED50CDD7A5EA7B68E723756AB3F32D0276B7EEC3085C104AB1F721805EE5E57BAA9BC3BEA4F7782EDCCA2BA4FAA5A372C849D516DBAC0777442BFB51670E5535C7986BD1F55D3902C93C5741C0BB4B00F3C8D7C736E994C838D2F5A847512005E12059AACEE4CA25350D520B6D4E101530AADBC0161E2EC4DB17D7CC0FA43A417275224E1AAD801DCC309D11CCA0BADA0ACDA2CD524CA5F968058A45AD64FC4DB7725B7A171CC831B5C3C65835796EBDFFCAF8885809CCD23ABCE1D08226D52EEBE4169F7594981BD1A3D0DDB792D52415372979CA2F7F3412E1DBFE9FDFCDAE410A5EE5AC4C47C3D7425450E61407E51C10882E31F48A10460581790436E7A0B04C3D7ADA3501702545C3C9BF63FC85CA65DD3152D98060E53492359678633C79FCEF73DAE1883DC5E823BB4191A362E7CF47D9F4D28AD946B413A37354A12CCCCAB33CAB8997ED4FCDD40F7C0F03F9F7AF7631A9C9156D774F6B109307FC42B36D1FD73FA183544A43C88D38F97E6FA161E9EF138E3C4B0A260C8F93685C75C358C4EAE328AF2CF5BBC6DB9D698C87A4BA921F2615980D6A4026512AAC547BF2EF326463B2B735CA541932B9D3B8CA5292C7E3C850C58FF8537CFCC11DCFA14D4F963B0B8CDDEB5E3D4E4CA61E656AA4F21F1673EEE79BE8687A5779B4AB5931FB440DE2885A390F9F8C3799FA1ACE15A9B2531FF3ABAECCF1801C225070E84DE9B005AE89A236464F3F0459949630A9A210CA809DC883AD19D43B578F5685368B6FEB396E0821E5E5DC6B3FE9855460AEC802C186F22CE124CB6750F3F3B79FDB2A3A403BF508B5B914399E3B1CB1610959253DDFAE5E5984BDCECC7DC7C35A6AC4878A3A9F67E2AF34C4748C9B1991235F51608336EBAABAB3F82C0E735D7EB8365004C1B42CF90C6C34424EDFD8C6474A6095B8024E4FC35465CEF966417D0CAA6F6131256F06C1C5F1F965F25017DB9C2A9EE31EB40FBEFDC8E186D2DE57F4AD2BF0F1700F8883174651AC894F5CECF53361E15E9724B792D5001E539A5C02A789C6F21B71E12942E0D756CB9E63260592627AE0393A9F7C8C883B8B5359BA2D7553BA9C424DDBFE4FF9B803093EEF0FA0F8D95ADE895C8D6B1B545665A571F58CF77609B26031E173751FE003E5E11559B3E12A321B93CC7EA01FE7268AB97F49B7BC6721BC5532206B441D9ECF3A262B834897DD00B24EE25E66921A1E5495B4C6ABEBBC5D33BD06FFE4DF63C9CDC949BBC63D94F39D63BE20329A17D9C94E01CA920E7B4DF51F4EA690FF6326417F8B238042612AA1FC81CCFA2852CBF121BBF84EFCB1F85CC7EC0A5CACA88");
	    byte[][] pk2 = instance.generatePublicKey(message, signature);

	    Assert.assertEquals(pk1.length, pk2.length);
	    for (int i = 0; i < pk1.length; i++) {
		Assert.assertEquals(pk1[i], pk2[i]);
	    }
	} catch (Exception e) {
	    logger.error("Exception", e);
	    Assert.fail(e.getMessage());
	}
    }
}
