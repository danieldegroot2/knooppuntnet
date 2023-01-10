package kpn.server.analyzer.engine.tiles.vector.encoder

import kpn.core.util.UnitTest
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory

import scala.collection.immutable.ListMap

class VectorTileEncoderTest extends UnitTest {

  private val geometryFactory = new GeometryFactory()

  test("Encode") {
    val geometry1 = {
      val cs = Array(
        new Coordinate(3, 6),
        new Coordinate(8, 12),
        new Coordinate(20, 34)
      )
      geometryFactory.createLineString(cs)
    }

    val geometry2 = {
      val cs = Array(
        new Coordinate(3, 6),
        new Coordinate(8, 12),
        new Coordinate(20, 34),
        new Coordinate(33, 72)
      )
      geometryFactory.createLineString(cs)
    }

    val obsoleteAttributes = new java.util.HashMap[String, String]()

    {
      val vtm = new VectorTileEncoder()
      //      vtm.addFeature("DEPCNT", ListMap(), geometry1)
      //      vtm.addFeature("DEPCNT", ListMap(), geometry2)
      //      val encoded = vtm.encode
      //      encoded.length should not equal (0)
    }
  }


  test("NullAttributeValue") {
    val vtm = new VectorTileEncoder()
    val geometry = geometryFactory.createPoint(new Coordinate(3, 6))
    val attributes = ListMap(
      "key1" -> "value1",
      "key2" -> null,
      "key3" -> "value3"
    )
    //    vtm.addFeature("DEPCNT", attributes, geometry)
    //    val encoded = vtm.encode
    //    encoded.length should not equal (0)
    //    val decoder = new Nothing
    //    assertEquals(1, decoder.decode(encoded, "DEPCNT").asList.size)
    //    val decodedAttributes = decoder.decode(encoded, "DEPCNT").asList.get(0).getAttributes
    //    assertEquals("value1", decodedAttributes.get("key1"))
    //    assertEquals("value3", decodedAttributes.get("key3"))
    //    assertFalse(decodedAttributes.containsKey("key2"))
  }

  test("AttributeTypes") {
    val vtm = new VectorTileEncoder()
    val geometry = geometryFactory.createPoint(new Coordinate(3, 6))
    val attributes = new java.util.HashMap[String, Object]()
    attributes.put("key1", "value1")
    attributes.put("key2", Integer.valueOf(123))
    attributes.put("key3", java.lang.Float.valueOf(234.1f))
    attributes.put("key4", java.lang.Double.valueOf(567.123d))
    attributes.put("key5", java.lang.Long.valueOf(-123L))
    attributes.put("key6", "value6")
    //    vtm.addFeature("DEPCNT", attributes, geometry)
    //    val encoded = vtm.encode
    //    encoded.length should not equal (0)
    //    val decoder = new Nothing
    //    assertEquals(1, decoder.decode(encoded, "DEPCNT").asList.size)
    //    val decodedAttributes = decoder.decode(encoded, "DEPCNT").asList.get(0).getAttributes
    //    assertEquals("value1", decodedAttributes.get("key1"))
    //    assertEquals(Long.valueOf(123), decodedAttributes.get("key2"))
    //    assertEquals(Float.valueOf(234.1f), decodedAttributes.get("key3"))
    //    assertEquals(Double.valueOf(567.123d), decodedAttributes.get("key4"))
    //    assertEquals(Long.valueOf(-123), decodedAttributes.get("key5"))
    //    assertEquals("value6", decodedAttributes.get("key6"))
  }

  test("add route feature") {

    val testdata = Seq(
      Seq(
        Seq(-428.1440665599994, 4037.258771698117),
        Seq(-426.09351793778023, 4036.48330726458),
        Seq(-426.09351793778023, 4036.48330726458),
        Seq(-396.18347008000643, 4021.771852184465),
        Seq(-396.18347008000643, 4021.771852184465),
        Seq(-377.21589532444875, 4011.6982709309996),
        Seq(-377.21589532444875, 4011.6982709309996),
        Seq(-367.149565724449, 4007.477954876621),
        Seq(-367.149565724449, 4007.477954876621),
        Seq(-360.56450844444333, 4008.081922368666),
        Seq(-360.56450844444333, 4008.081922368666),
        Seq(-341.46644423111445, 3956.9534644455553),
        Seq(-341.46644423111445, 3956.9534644455553),
        Seq(-329.3728676977878, 3945.8956398721525),
        Seq(-329.3728676977878, 3945.8956398721525),
        Seq(-318.7566182399996, 3941.488914098),
        Seq(-318.7566182399996, 3941.488914098),
        Seq(-263.75531178667313, 3920.976388540399),
        Seq(-263.75531178667313, 3920.976388540399),
        Seq(-208.11087872001033, 3922.691357961679),
        Seq(-208.11087872001033, 3922.691357961679),
        Seq(-199.82945848889648, 3902.894645728199),
        Seq(-199.82945848889648, 3902.894645728199),
        Seq(-200.3700576711032, 3888.4739898110574),
        Seq(-200.3700576711032, 3888.4739898110574),
        Seq(-195.22504476445417, 3872.181780308365),
        Seq(-195.22504476445417, 3872.181780308365),
        Seq(-170.94468494223224, 3822.156867925614),
        Seq(-170.94468494223224, 3822.156867925614),
        Seq(-161.75915918221904, 3805.775181756943),
        Seq(-161.75915918221904, 3805.775181756943),
        Seq(-155.8032475022185, 3802.151376805733),
        Seq(-155.8032475022185, 3802.151376805733),
        Seq(-93.28947655111551, 3796.2310041078254),
        Seq(-93.28947655111551, 3796.2310041078254),
        Seq(-94.92059477333807, 3774.8535374507082),
        Seq(-94.92059477333807, 3774.8535374507082),
        Seq(-96.4445252266609, 3755.5042085444725),
        Seq(-96.4445252266609, 3755.5042085444725),
        Seq(-99.07761607110086, 3732.9560888481737),
        Seq(-99.07761607110086, 3732.9560888481737),
        Seq(-100.05162666667667, 3720.190751241837),
        Seq(-100.05162666667667, 3720.190751241837),
        Seq(-98.39254641777111, 3716.9621348962714),
        Seq(-98.39254641777111, 3716.9621348962714),
        Seq(-102.47034197333787, 3709.520658885203),
        Seq(-102.47034197333787, 3709.520658885203),
        Seq(-99.08227640889172, 3707.835515019425),
        Seq(-99.08227640889172, 3707.835515019425),
        Seq(-103.42105088000082, 3701.214241775148),
        Seq(-103.42105088000082, 3701.214241775148),
        Seq(-108.98549418666711, 3695.5548426841815),
        Seq(-108.98549418666711, 3695.5548426841815),
        Seq(-112.0846188088879, 3699.2755806897158),
        Seq(-112.0846188088879, 3699.2755806897158),
        Seq(-115.80356835555285, 3695.3758893532836),
        Seq(-115.80356835555285, 3695.3758893532836),
        Seq(-117.52323299555314, 3693.1315163281856),
        Seq(-117.52323299555314, 3693.1315163281856),
        Seq(-118.74890183111032, 3690.7827538598876),
        Seq(-118.74890183111032, 3690.7827538598876),
        Seq(-119.61106431999555, 3686.5848069716058),
        Seq(-119.61106431999555, 3686.5848069716058),
        Seq(-119.05648412443698, 3682.051322588156),
        Seq(-119.05648412443698, 3682.051322588156),
        Seq(-117.03855786667101, 3678.1889131961025),
        Seq(-117.03855786667101, 3678.1889131961025),
        Seq(-47.11951018667055, 3598.63670121045),
        Seq(-47.11951018667055, 3598.63670121045),
        Seq(-29.4987730488802, 3577.5574901051828),
        Seq(-29.4987730488802, 3577.5574901051828),
        Seq(-20.03362702222334, 3564.978562219149),
        Seq(-20.03362702222334, 3564.978562219149),
        Seq(-18.626205013340545, 3562.599974195348),
        Seq(-18.626205013340545, 3562.599974195348),
        Seq(-19.10155946666168, 3561.250367824739),
        Seq(-19.10155946666168, 3561.250367824739),
        Seq(-24.32579811554816, 3558.7748467466135),
        Seq(-24.32579811554816, 3558.7748467466135),
        Seq(-23.277222115546465, 3553.786522647573),
        Seq(-23.277222115546465, 3553.786522647573),
        Seq(-22.27524949332906, 3550.9605512965272),
        Seq(-22.27524949332906, 3550.9605512965272),
        Seq(-14.977160533331334, 3543.571270007059),
        Seq(-14.977160533331334, 3543.571270007059),
        Seq(-13.005837653328975, 3540.163700330596),
        Seq(-13.005837653328975, 3540.163700330596),
        Seq(-8.373461902224355, 3528.2856729904015),
        Seq(-8.373461902224355, 3528.2856729904015),
        Seq(-11.351417742214268, 3521.873178631995),
        Seq(-11.351417742214268, 3521.873178631995),
        Seq(-15.149593031116657, 3514.439159009802),
        Seq(-15.149593031116657, 3514.439159009802),
        Seq(-8.033257244444556, 3510.1666482343526),
        Seq(-8.033257244444556, 3510.1666482343526),
        Seq(-10.055843840001359, 3506.51301772764),
        Seq(-10.055843840001359, 3506.51301772764),
        Seq(-10.862082275561988, 3505.088847469334),
        Seq(-10.862082275561988, 3505.088847469334),
        Seq(-18.54231893333296, 3500.5180811415053),
        Seq(-18.54231893333296, 3500.5180811415053),
        Seq(-20.406454044435588, 3484.9491413512796),
        Seq(-20.406454044435588, 3484.9491413512796),
        Seq(-21.86047943110267, 3475.1961848157616),
        Seq(-21.86047943110267, 3475.1961848157616),
        Seq(-22.89973475556407, 3469.178879063529),
        Seq(-22.89973475556407, 3469.178879063529),
        Seq(-39.49519758222418, 3450.9107265315556),
        Seq(-39.49519758222418, 3450.9107265315556),
        Seq(-43.852613404434585, 3446.764974364874),
        Seq(-43.852613404434585, 3446.764974364874),
        Seq(-49.244624213336245, 3442.194208037575),
        Seq(-49.244624213336245, 3442.194208037575),
        Seq(-76.56352426666352, 3426.7072885239227),
        Seq(-76.56352426666352, 3426.7072885239227),
        Seq(-80.38966158222821, 3423.694907453104),
        Seq(-80.38966158222821, 3423.694907453104),
        Seq(-83.56801194666575, 3420.02636416917),
        Seq(-83.56801194666575, 3420.02636416917),
        Seq(-87.93474844443715, 3407.8053429444017),
        Seq(-87.93474844443715, 3407.8053429444017),
        Seq(-84.12725247999447, 3407.179006286259),
        Seq(-84.12725247999447, 3407.179006286259),
        Seq(-65.39269461333751, 3405.978527691575),
        Seq(-65.39269461333751, 3405.978527691575),
        Seq(-56.06735871999214, 3406.3737162972193),
        Seq(-56.06735871999214, 3406.3737162972193),
        Seq(-44.854586026672685, 3407.9544707203263),
        Seq(-44.854586026672685, 3407.9544707203263),
        Seq(-33.073252124442824, 3408.4689615469224),
        Seq(-33.073252124442824, 3408.4689615469224),
        Seq(-21.315219911105103, 3407.73823544558),
        Seq(-21.315219911105103, 3407.73823544558),
        Seq(-11.174324906658795, 3405.5460571415524),
        Seq(-11.174324906658795, 3405.5460571415524),
        Seq(-3.2377696711156103, 3402.3025280187653),
        Seq(-3.2377696711156103, 3402.3025280187653),
        Seq(2.065694720008307, 3399.4616438899684),
        Seq(2.065694720008307, 3399.4616438899684),
        Seq(4.419165297783911, 3393.817157577283),
        Seq(4.419165297783911, 3393.817157577283),
        Seq(5.971057777769036, 3388.0906509874944),
        Seq(5.971057777769036, 3388.0906509874944),
        Seq(6.8658426311074034, 3382.431251897058),
        Seq(6.8658426311074034, 3382.431251897058),
        Seq(6.9730304000071355, 3377.741183349337),
        Seq(6.9730304000071355, 3377.741183349337),
        Seq(6.828559928884109, 3373.6849078481046),
        Seq(6.828559928884109, 3373.6849078481046),
        Seq(6.171452302216656, 3370.6352448334364),
        Seq(6.171452302216656, 3370.6352448334364),
        Seq(3.5942855111012855, 3365.0951479639516),
        Seq(3.5942855111012855, 3365.0951479639516),
        Seq(-0.2318518044427037, 3358.2129011116735),
        Seq(-0.2318518044427037, 3358.2129011116735),
        Seq(-2.1519109688906206, 3353.776349782548),
        Seq(-2.1519109688906206, 3353.776349782548),
        Seq(-3.8109912177754772, 3347.1327073721736),
        Seq(-3.8109912177754772, 3347.1327073721736),
        Seq(-4.561305599990818, 3342.3382493807226)
      ),
      Seq(
        Seq(-4.561305599990818, 3342.3382493807226),
        Seq(-3.8109912177754772, 3347.1327073721736),
        Seq(-3.8109912177754772, 3347.1327073721736),
        Seq(-2.1519109688906206, 3353.776349782548),
        Seq(-2.1519109688906206, 3353.776349782548),
        Seq(-0.2318518044427037, 3358.2129011116735),
        Seq(-0.2318518044427037, 3358.2129011116735),
        Seq(3.5942855111012855, 3365.0951479639516),
        Seq(3.5942855111012855, 3365.0951479639516),
        Seq(6.171452302216656, 3370.6352448334364),
        Seq(6.171452302216656, 3370.6352448334364),
        Seq(6.828559928884109, 3373.6849078481046),
        Seq(6.828559928884109, 3373.6849078481046),
        Seq(6.9730304000071355, 3377.741183349337),
        Seq(6.9730304000071355, 3377.741183349337),
        Seq(6.8658426311074034, 3382.431251897058),
        Seq(6.8658426311074034, 3382.431251897058),
        Seq(5.971057777769036, 3388.0906509874944),
        Seq(5.971057777769036, 3388.0906509874944),
        Seq(4.419165297783911, 3393.817157577283),
        Seq(4.419165297783911, 3393.817157577283),
        Seq(2.065694720008307, 3399.4616438899684),
        Seq(2.065694720008307, 3399.4616438899684),
        Seq(-3.2377696711156103, 3402.3025280187653),
        Seq(-3.2377696711156103, 3402.3025280187653),
        Seq(-11.174324906658795, 3405.5460571415524),
        Seq(-11.174324906658795, 3405.5460571415524),
        Seq(-21.315219911105103, 3407.73823544558),
        Seq(-21.315219911105103, 3407.73823544558),
        Seq(-33.073252124442824, 3408.4689615469224),
        Seq(-33.073252124442824, 3408.4689615469224),
        Seq(-44.854586026672685, 3407.9544707203263),
        Seq(-44.854586026672685, 3407.9544707203263),
        Seq(-56.06735871999214, 3406.3737162972193),
        Seq(-56.06735871999214, 3406.3737162972193),
        Seq(-65.39269461333751, 3405.978527691575),
        Seq(-65.39269461333751, 3405.978527691575),
        Seq(-84.12725247999447, 3407.179006286259),
        Seq(-84.12725247999447, 3407.179006286259),
        Seq(-87.93474844443715, 3407.8053429444017),
        Seq(-87.93474844443715, 3407.8053429444017),
        Seq(-83.56801194666575, 3420.02636416917),
        Seq(-83.56801194666575, 3420.02636416917),
        Seq(-80.38966158222821, 3423.694907453104),
        Seq(-80.38966158222821, 3423.694907453104),
        Seq(-76.56352426666352, 3426.7072885239227),
        Seq(-76.56352426666352, 3426.7072885239227),
        Seq(-49.244624213336245, 3442.194208037575),
        Seq(-49.244624213336245, 3442.194208037575),
        Seq(-43.852613404434585, 3446.764974364874),
        Seq(-43.852613404434585, 3446.764974364874),
        Seq(-39.49519758222418, 3450.9107265315556),
        Seq(-39.49519758222418, 3450.9107265315556),
        Seq(-22.89973475556407, 3469.178879063529),
        Seq(-22.89973475556407, 3469.178879063529),
        Seq(-21.86047943110267, 3475.1961848157616),
        Seq(-21.86047943110267, 3475.1961848157616),
        Seq(-20.406454044435588, 3484.9491413512796),
        Seq(-20.406454044435588, 3484.9491413512796),
        Seq(-18.54231893333296, 3500.5180811415053),
        Seq(-18.54231893333296, 3500.5180811415053),
        Seq(-10.862082275561988, 3505.088847469334),
        Seq(-10.862082275561988, 3505.088847469334),
        Seq(-10.055843840001359, 3506.51301772764),
        Seq(-10.055843840001359, 3506.51301772764),
        Seq(-8.033257244444556, 3510.1666482343526),
        Seq(-8.033257244444556, 3510.1666482343526),
        Seq(-15.149593031116657, 3514.439159009802),
        Seq(-15.149593031116657, 3514.439159009802),
        Seq(-11.351417742214268, 3521.873178631995),
        Seq(-11.351417742214268, 3521.873178631995),
        Seq(-8.373461902224355, 3528.2856729904015),
        Seq(-8.373461902224355, 3528.2856729904015),
        Seq(-13.005837653328975, 3540.163700330596),
        Seq(-13.005837653328975, 3540.163700330596),
        Seq(-14.977160533331334, 3543.571270007059),
        Seq(-14.977160533331334, 3543.571270007059),
        Seq(-22.27524949332906, 3550.9605512965272),
        Seq(-22.27524949332906, 3550.9605512965272),
        Seq(-23.277222115546465, 3553.786522647573),
        Seq(-23.277222115546465, 3553.786522647573),
        Seq(-24.32579811554816, 3558.7748467466135),
        Seq(-24.32579811554816, 3558.7748467466135),
        Seq(-19.10155946666168, 3561.250367824739),
        Seq(-19.10155946666168, 3561.250367824739),
        Seq(-18.626205013340545, 3562.599974195348),
        Seq(-18.626205013340545, 3562.599974195348),
        Seq(-20.03362702222334, 3564.978562219149),
        Seq(-20.03362702222334, 3564.978562219149),
        Seq(-29.4987730488802, 3577.5574901051828),
        Seq(-29.4987730488802, 3577.5574901051828),
        Seq(-47.11951018667055, 3598.63670121045),
        Seq(-47.11951018667055, 3598.63670121045),
        Seq(-117.03855786667101, 3678.1889131961025),
        Seq(-117.03855786667101, 3678.1889131961025),
        Seq(-119.05648412443698, 3682.051322588156),
        Seq(-119.05648412443698, 3682.051322588156),
        Seq(-119.61106431999555, 3686.5848069716058),
        Seq(-119.61106431999555, 3686.5848069716058),
        Seq(-118.74890183111032, 3690.7827538598876),
        Seq(-118.74890183111032, 3690.7827538598876),
        Seq(-117.52323299555314, 3693.1315163281856),
        Seq(-117.52323299555314, 3693.1315163281856),
        Seq(-115.80356835555285, 3695.3758893532836),
        Seq(-115.80356835555285, 3695.3758893532836),
        Seq(-112.0846188088879, 3699.2755806897158),
        Seq(-112.0846188088879, 3699.2755806897158),
        Seq(-108.98549418666711, 3695.5548426841815),
        Seq(-108.98549418666711, 3695.5548426841815),
        Seq(-103.42105088000082, 3701.214241775148),
        Seq(-103.42105088000082, 3701.214241775148),
        Seq(-99.08227640889172, 3707.835515019425),
        Seq(-99.08227640889172, 3707.835515019425),
        Seq(-102.47034197333787, 3709.520658885203),
        Seq(-102.47034197333787, 3709.520658885203),
        Seq(-98.39254641777111, 3716.9621348962714),
        Seq(-98.39254641777111, 3716.9621348962714),
        Seq(-100.05162666667667, 3720.190751241837),
        Seq(-100.05162666667667, 3720.190751241837),
        Seq(-99.07761607110086, 3732.9560888481737),
        Seq(-99.07761607110086, 3732.9560888481737),
        Seq(-96.4445252266609, 3755.5042085444725),
        Seq(-96.4445252266609, 3755.5042085444725),
        Seq(-94.92059477333807, 3774.8535374507082),
        Seq(-94.92059477333807, 3774.8535374507082),
        Seq(-93.28947655111551, 3796.2310041078254),
        Seq(-93.28947655111551, 3796.2310041078254),
        Seq(-155.8032475022185, 3802.151376805733),
        Seq(-155.8032475022185, 3802.151376805733),
        Seq(-161.75915918221904, 3805.775181756943),
        Seq(-161.75915918221904, 3805.775181756943),
        Seq(-170.94468494223224, 3822.156867925614),
        Seq(-170.94468494223224, 3822.156867925614),
        Seq(-195.22504476445417, 3872.181780308365),
        Seq(-195.22504476445417, 3872.181780308365),
        Seq(-200.3700576711032, 3888.4739898110574),
        Seq(-200.3700576711032, 3888.4739898110574),
        Seq(-199.82945848889648, 3902.894645728199),
        Seq(-199.82945848889648, 3902.894645728199),
        Seq(-208.11087872001033, 3922.691357961679),
        Seq(-208.11087872001033, 3922.691357961679),
        Seq(-263.75531178667313, 3920.976388540399),
        Seq(-263.75531178667313, 3920.976388540399),
        Seq(-318.7566182399996, 3941.488914098),
        Seq(-318.7566182399996, 3941.488914098),
        Seq(-329.3728676977878, 3945.8956398721525),
        Seq(-329.3728676977878, 3945.8956398721525),
        Seq(-341.46644423111445, 3956.9534644455553),
        Seq(-341.46644423111445, 3956.9534644455553),
        Seq(-360.56450844444333, 4008.081922368666),
        Seq(-360.56450844444333, 4008.081922368666),
        Seq(-367.149565724449, 4007.477954876621),
        Seq(-367.149565724449, 4007.477954876621),
        Seq(-377.21589532444875, 4011.6982709309996),
        Seq(-377.21589532444875, 4011.6982709309996),
        Seq(-396.18347008000643, 4021.771852184465),
        Seq(-396.18347008000643, 4021.771852184465),
        Seq(-426.09351793778023, 4036.48330726458),
        Seq(-426.09351793778023, 4036.48330726458),
        Seq(-428.1440665599994, 4037.258771698117)
      )
    )

    val geomFactory = new GeometryFactory

    var previousLine: Seq[Double] = Seq.empty

    val lineStrings = testdata.map { segment =>
      val coordinates = segment.flatMap { line =>
        if (line != previousLine) {
          previousLine = line
          Some(new Coordinate(line(0), line(1)))
        }
        else {
          None
        }
      }
      geomFactory.createLineString(coordinates.toArray)
    }

    val geometry = geomFactory.createMultiLineString(lineStrings.toArray)

    val newEncoder = new VectorTileEncoder()

    newEncoder.addMultiLineStringFeature("test", ListMap(), geometry)

    val newBytes = newEncoder.encode

    val newBytesHex = newBytes.map("%02X" format _).mkString(" ")

  }
}
