package com.example.data.service

import com.example.data.model.ScanResult

object AgronomyKnowledgeBase {

  /**
   * Generates a grounded, practical diagnosis in simple everyday Hindi based on crop and user symptoms.
   */
  fun diagnose(
    cropName: String,
    symptomLocation: String, // "पत्तियों में", "फूलों में", "फल में", "तने में", "जड़ में", "पूरे पौधे में"
    plantAge: String, // "0–30 दिन", "1–2 महीने", "2–3 महीने", "3 महीने से ज्यादा", "पता नहीं"
    hasInsects: String, // "हाँ", "नहीं", "पता नहीं"
    flowerDropping: String, // "हाँ", "नहीं", "पता नहीं"
    leavesYellowing: String, // "हाँ", "नहीं", "पता नहीं"
    freeFormText: String = "",
    isBlurry: Boolean = false
  ): ScanResult {
    if (isBlurry) {
      return ScanResult(
        cropName = cropName,
        problemHindi = "फोटो साफ नहीं है",
        confidenceHindi = "कम संभावना",
        possibleCausesRaw = "• फोटो हिल गई है या बहुत दूर से ली गई है\n• रोशनी कम होने से पत्ते की नसें स्पष्ट नहीं दिख रहीं",
        visibleSymptomsRaw = "• धुंधली छवि",
        recommendedActionsRaw = "1. पौधे के नजदीक जाएं (15 से 20 सेमी दूर)।\n2. पर्याप्त धूप या उजाले में प्रभावित पत्ते या फूल की साफ फोटो लें।\n3. कैमरा फोकस स्थिर होने के बाद ही फोटो खींचें।",
        thingsToAvoidRaw = "• धुंधली या छांव वाली फोटो पर कोई भी रासायनिक दवा न छिड़कें।",
        whenToRecheckHindi = "साफ फोटो लेकर दोबारा स्कैन करें",
        expertRequired = false,
        expertReasonHindi = "",
        isDemoMode = true
      )
    }

    val query = "${freeFormText} ${cropName} ${symptomLocation}".lowercase()

    // 1. Specific condition checks
    return when {
      // Flower dropping condition
      flowerDropping == "हाँ" || symptomLocation.contains("फूल") || query.contains("फूल") -> {
        ScanResult(
          cropName = cropName,
          problemHindi = "फूल झड़ना (Flower Drop / बत्तियां गिरना)",
          confidenceHindi = if (flowerDropping == "हाँ") "उच्च संभावना" else "मध्यम संभावना",
          possibleCausesRaw = "• खेत में नमी का असंतुलन (बहुत ज्यादा या कम पानी)\n• दिन में तेज धूप व रात के तापमान में अंतर\n• सूक्ष्म पोषक तत्वों (बोरोन या कैल्शियम) की कमी\n• रस चूसक कीट (थ्रिप्स/सफेद मक्खी) द्वारा फूलों का रस चूसना\n• तेज हवा या परागण (Pollination) न हो पाना",
          visibleSymptomsRaw = "• खिले हुए फूल पीले होकर डंठल से अलग हो जाते हैं\n• फल बनने से पहले ही फूल गिर जाते हैं\n• फूल के पिछले हिस्से पर काले या भूरे चकत्ते हो सकते हैं",
          recommendedActionsRaw = "1. सिंचाई का समय नियमित करें, मिट्टी में जलभराव बिल्कुल न होने दें।\n2. शाम के समय हल्की नमी बनाए रखें ताकि परागण अच्छा हो सके।\n3. जैविक उपचार के रूप में 2 मिली प्रति लीटर नीम का तेल (1500 PPM) का छिड़काव करें।\n4. बोरोन (20%) का 1 ग्राम प्रति लीटर पानी में घोल बनाकर शाम को छिड़कें।\n5. फूलों में बारीक कीड़ों (थ्रिप्स) की जांच सफेद कागज पर फूल झाड़कर करें।",
          thingsToAvoidRaw = "• फूल आते समय बहुत तेज रासायनिक कीटनाशकों का छिड़काव न करें, इससे मित्र कीट (मधुमक्खियां) मर जाती हैं।\n• बहुत अधिक यूरिया (नाइट्रोजन) न डालें, इससे केवल पत्तियां बढ़ेंगी और फूल गिरेंगे।",
          whenToRecheckHindi = "3 से 5 दिन बाद नई कलियों की जांच करें",
          expertRequired = false,
          expertReasonHindi = "",
          isDemoMode = true
        )
      }

      // Fruit not forming
      query.contains("फल नहीं") || query.contains("फल न") || (symptomLocation.contains("फल") && flowerDropping != "हाँ" && hasInsects != "हाँ") -> {
        ScanResult(
          cropName = cropName,
          problemHindi = "फल न बनना / परागण की कमी (Poor Fruit Setting)",
          confidenceHindi = "मध्यम संभावना",
          possibleCausesRaw = "• फूल से फल में बदलने हेतु प्राकृतिक परागण (मधुमक्खी/हवा) न होना\n• तापमान 35°C से अधिक होना जिससे परागकण सूख जाते हैं\n• पोटाश और सूक्ष्म पोषक तत्वों की कमी\n• नर और मादा फूलों का अनुपात असंतुलित होना",
          visibleSymptomsRaw = "• फूल तो आते हैं लेकिन छोटे फल बनने से पहले मुरझा जाते हैं\n• फल टेढ़े-मेढ़े या छोटे रह जाते हैं",
          recommendedActionsRaw = "1. खेत के मेड़ों पर पीले और गेंदे के फूल लगाएं ताकि मधुमक्खियां आएं।\n2. बेल वाली फसलों (खीरा/लौकी/करेला) में सुबह 7 से 9 बजे के बीच हल्के हाथ से कृत्रिम परागण कर सकते हैं।\n3. पोटाश युक्त जैविक खाद या NPK (0:52:34) की अनुशंसित मात्रा दें।\n4. पौधे की जड़ के पास पर्याप्त हवा व हल्की नमी रखें।",
          thingsToAvoidRaw = "• सुबह के समय तेज कीटनाशक का छिड़काव न करें जिससे परागण करने वाले कीट दूर भाग जाएं।\n• अत्यधिक जलभराव से बचें।",
          whenToRecheckHindi = "5 से 7 दिन बाद छोटे फलों के विकास को देखें",
          expertRequired = false,
          expertReasonHindi = "",
          isDemoMode = true
        )
      }

      // Yellow Leaves condition
      leavesYellowing == "हाँ" || symptomLocation.contains("पत्तियों") && query.contains("पील") -> {
        val specificDisease = when (cropName) {
          "भिंडी" -> "पीला शिरा मोज़ेक रोग (Yellow Vein Mosaic Virus)"
          "मिर्च", "टमाटर" -> "लीफ कर्ल वायरस / रस चूसक कीट का असर"
          "खीरा", "लौकी" -> "डाउनी मिल्ड्यू या जड़ में पानी भरना"
          else -> "पत्तियों का पीलापन व पोषण असंतुलन"
        }
        ScanResult(
          cropName = cropName,
          problemHindi = specificDisease,
          confidenceHindi = "उच्च संभावना",
          possibleCausesRaw = "• मिट्टी में अधिक समय तक पानी जमा रहना या जड़ का दम घुटना\n• सफेद मक्खी या एफिड्स द्वारा वायरस का फैलाव\n• नाइट्रोजन या मैग्नीशियम/आयरन पोषक तत्व की कमी\n• पुरानी पत्तियों की प्राकृतिक उम्र",
          visibleSymptomsRaw = "• ऊपरी या निचली पत्तियां हल्के हरे से पीले रंग में बदल रही हैं\n• नसों के बीच का हिस्सा पीला और नसें हरी दिखाई दे सकती हैं\n• पत्तियां किनारों से मुड़ने लगी हैं",
          recommendedActionsRaw = "1. सबसे पहले खेत से अतिरिक्त पानी निकालने की व्यवस्था करें।\n2. पत्तियों के नीचे सफेद मक्खी या माहो (एफिड्स) देखें; पीला चिपचिपा ट्रैप (Yellow Sticky Trap) 10-12 प्रति एकड़ लगाएं।\n3. जैविक रूप से खट्टी छाछ (मट्ठा) 50 मिली प्रति लीटर पानी में मिलाकर छिड़कें।\n4. वर्मीकम्पोस्ट या अच्छी सड़ी गोबर की खाद डालें।",
          thingsToAvoidRaw = "• बिना मिट्टी की जांच किए सीधे अंधाधुंध यूरिया न डालें।\n• दोपहर की तेज धूप में छिड़काव न करें।",
          whenToRecheckHindi = "4 दिन बाद नई पत्तियों के रंग की जांच करें",
          expertRequired = specificDisease.contains("वायरस"),
          expertReasonHindi = if (specificDisease.contains("वायरस")) "वायरस जनित रोग तेजी से फैल सकते हैं, तुरंत ग्रसित पौधे उखाड़कर नष्ट करें और कृषि वैज्ञानिक से संपर्क करें।" else "",
          isDemoMode = true
        )
      }

      // Insects / Pests detected
      hasInsects == "हाँ" || symptomLocation.contains("कीड़े") || query.contains("कीड़े") || query.contains("इल्ली") -> {
        val pestName = when (cropName) {
          "टमाटर", "बैंगन" -> "फल व तना छेदक इल्ली (Fruit & Shoot Borer)"
          "मिर्च" -> "थ्रिप्स व माइट्स (रस चूसक कीट)"
          "गोभी" -> "हीरक पतंगा (Diamondback Moth) की इल्ली"
          else -> "रस चूसक कीट व इल्ली (Pest Infestation)"
        }
        ScanResult(
          cropName = cropName,
          problemHindi = "कीट प्रकोप: $pestName",
          confidenceHindi = "उच्च संभावना",
          possibleCausesRaw = "• मौसम में अधिक उमस व नमी\n• कीटों के अंडों से इल्लियों का निकलना\n• खेत के आसपास खरपतवार का होना जहां कीट पनपते हैं",
          visibleSymptomsRaw = "• पत्तियों पर बारीक जाले या कटे हुए छेद दिखाई देते हैं\n• फल या तने में बारीक छेद और बाहर भूसी जैसा मल दिखना\n• पत्तियां नाव के आकार में ऊपर या नीचे मुड़ना",
          recommendedActionsRaw = "1. प्राथमिक स्तर पर फेरोमोन ट्रैप (Pheromone Trap) 5-6 प्रति एकड़ लगाएं।\n2. ग्रसित फलों और टहनियों को तोड़कर खेत से दूर जमीन में दबा दें।\n3. नीम का काढ़ा या 5% नीम बीज अर्क (NSKE) का छिड़काव करें।\n4. रासायनिक कीटनाशक केवल विशेषज्ञ की पर्ची और उत्पाद लेबल के अनुसार ही प्रयोग करें।",
          thingsToAvoidRaw = "• अत्यधिक और बिना नापे कीटनाशक का भारी डोज न डालें।\n• एक ही दवा बार-बार न छिड़कें ताकि कीटों में प्रतिरोधी क्षमता न बने।",
          whenToRecheckHindi = "3 दिन बाद छिड़काव का प्रभाव देखें",
          expertRequired = true,
          expertReasonHindi = "यदि फल के भीतर इल्ली प्रवेश कर चुकी है, तो सही सुरक्षित नियंत्रण हेतु स्थानीय कृषि विज्ञान केंद्र से संपर्क करें।",
          isDemoMode = true
        )
      }

      // Crop specific defaults
      cropName == "मिर्च" -> {
        ScanResult(
          cropName = "मिर्च",
          problemHindi = "पत्ती मरोड़िया रोग (Chilli Leaf Curl & Thrips)",
          confidenceHindi = "मध्यम संभावना",
          possibleCausesRaw = "• सफेद मक्खी और थ्रिप्स कीटों का रस चूसना\n• वायरस का संक्रमण\n• अधिक तापमान और शुष्क मौसम",
          visibleSymptomsRaw = "• मिर्च के पत्ते ऊपर की ओर मुड़कर नाव जैसी आकृति बना लेते हैं\n• पौधे की बढ़वार रुक जाती है\n• नए पत्ते छोटे व झुर्रीदार निकलते हैं",
          recommendedActionsRaw = "1. पीला व नीला स्टिकी ट्रैप खेत में लगाएं।\n2. प्रारंभिक अवस्था में ग्रसित पौधे को उखाड़कर खेत से दूर नष्ट करें।\n3. नीम तेल 3 मिली प्रति लीटर का 4 दिन के अंतराल पर 2 बार छिड़काव करें।\n4. संतुलित सिंचाई दें।",
          thingsToAvoidRaw = "• ग्रसित पत्तों को हाथ से मसलकर दूसरे स्वस्थ पौधों को न छुएं।\n• नाइट्रोजन खाद अधिक न दें।",
          whenToRecheckHindi = "5 दिन में नई पत्तियों का फुटाव देखें",
          expertRequired = false,
          isDemoMode = true
        )
      }

      cropName == "टमाटर" -> {
        ScanResult(
          cropName = "टमाटर",
          problemHindi = "अगेती / पछेती झुलसा (Tomato Blight / Fungal Spot)",
          confidenceHindi = "मध्यम संभावना",
          possibleCausesRaw = "• पत्तियों पर अधिक देर तक पानी ठहरना\n• फफूंद (Fungus) का संक्रमण\n• हवा में अत्यधिक नमी और ठंडा-गर्म मौसम",
          visibleSymptomsRaw = "• पत्तियों पर गोल भूरे-काले छल्लेदार धब्बे बनना\n• नीचे की पत्तियां सूखकर लटकना\n• तने पर काले दाग उभरना",
          recommendedActionsRaw = "1. पौधे के नीचे की मिट्टी से छूने वाली सूखी पत्तियां काट दें।\n2. सिंचाई हमेशा पौधे की जड़ के पास करें, पत्तियों पर पानी न छिड़कें।\n3. ट्राइकोडर्मा (Trichoderma) जैविक फफूंदनाशी 5 ग्राम प्रति लीटर पानी में घोलकर जड़ों के पास दें।\n4. कॉपर ऑक्सीक्लोराइड का लेबल निर्देशानुसार सुरक्षित प्रयोग कर सकते हैं।",
          thingsToAvoidRaw = "• रात के समय फव्वारा सिंचाई न करें।\n• संक्रमित पत्तियों को खेत में खुला न छोड़ें।",
          whenToRecheckHindi = "4 दिन बाद धब्बों के फैलाव की जांच करें",
          expertRequired = false,
          isDemoMode = true
        )
      }

      else -> {
        ScanResult(
          cropName = cropName,
          problemHindi = "पोषक तत्व व नमी असंतुलन (Nutrient & Moisture Stress)",
          confidenceHindi = "मध्यम संभावना",
          possibleCausesRaw = "• जड़ों में हवा व पानी का सही संतुलन न होना\n• मिट्टी में सूक्ष्म पोषक तत्वों (जिंक/बोरोन) की कमी\n• अचानक मौसम में बदलाव या तापमान का उतार-चढ़ाव",
          visibleSymptomsRaw = "• पौधे की स्वाभाविक चमक कम होना\n• पत्तियां हल्की पीली या किनारे सूखे दिखना\n• फूल और नई कलियों का विकास धीमा होना",
          recommendedActionsRaw = "1. खेत की मिट्टी को हाथ में लेकर नमी जांचें—लड्डू बने तो नमी पर्याप्त है।\n2. 10 दिन के अंतराल पर जीवामृत या जैविक खाद का प्रयोग करें।\n3. खरपतवार हटाकर पौधे के चारों तरफ हल्की निराई-गुड़ाई करें ताकि जड़ों को हवा मिले।",
          thingsToAvoidRaw = "• पौधे के तने से बिल्कुल सटाकर खाद न डालें।\n• दोपहर के समय तेज धूप में पानी न लगाएं।",
          whenToRecheckHindi = "7 दिन बाद पौधे की नई वृद्धि देखें",
          expertRequired = false,
          isDemoMode = true
        )
      }
    }
  }
}
