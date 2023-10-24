define(function () { return function(evaluationEx){
  var v1;
  var v2;
  var v3;
  with (evaluationEx) {
    var ctx = {
      localCountryRegex: "[Zz][Aa]", 
      localCurrencyRegex: "[Zz][Aa][Rr]", 
      whoAmIRegex: "SBZAZA.*", 
      onshoreRegex: "....ZA.*", 
      onshoreADRegex: ["ABSAZAJJ.*", "ALBRZAJJ.*", "BKCHZAJJ.*", "BIDBZAJJ.*", "BNPAZAJJ.*", "CABLZAJJ.*", "CITIZAJX.*", "FIRNZAJJ.*", "HOBLZAJJ.*", "HBZHZAJJ.*", "IVESZAJJ.*", "MGTCZAJJ.*", "LISAZAJJ.*", "NEDSZAJJ.*", "SASFZAJJ.*", "SOGEZAJJ.*", "SCBLZAJJ.*", "SBINZAJJ.*", "BATHZAJJ.*", "SBSAZAJJ.*"], 
      CMARegex: ["....NA.*", "....SZ.*", "....LS.*"], 
      ZZ1Reportability: true
    };
    v1 = [
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.NOSTRO], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA, accType.NOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.NR_OTH], 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA, at.RE_CFC]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.VOSTRO], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        validFrom: "2013-07-18", 
        crDecision: {
          manualSection: "B and T Section B.1 (A and B) - pages 1, 2, 3", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.VOSTRO], 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA]
        }, 
        drDecision: {
          manualSection: "B and T Section B.1 (B ix) - page 7", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.VOSTRO], 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA], 
          drSideSwift: "NTNRC"
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.NOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.REPORTABLE, 
          reportingSide: drcr.DR, 
          flow: flowDir.OUT, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.NR_OTH], 
          resSide: drcr.DR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.LOCAL_ACC], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_CURR], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Not stated in SARB specification, but seems similar to B and T Section B.1 (A) - page 1 (res rand to non res nostro)", 
          reportable: rep.REPORTABLE, 
          reportingSide: drcr.DR, 
          flow: flowDir.OUT, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at._CASH_], 
          resSide: drcr.DR, 
          resAccountType: [at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.VOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A and B) - pages 1, 2, 3", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.VOSTRO], 
          resSide: drcr.DR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.VOSTRO, accType.NOSTRO, accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - pages 1; B and T Section B.1 (C) - pages 14, 15", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.NR_RND, at.NR_RND, at.NR_RND], 
          resException: "NON RESIDENT RAND"
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.FCA, accType.VOSTRO, accType.NOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (C) - pages 14, 15; Manual Section B2 - A (iv) (a)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.NR_RND, at.NR_RND, at.NR_RND], 
          resException: "NON RESIDENT RAND"
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Not defined in manual explicitly", 
          reportable: rep.NONREPORTABLE, 
          drSideSwift: "NTNRC"
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A, B and C) - all pages", 
          reportable: rep.NONREPORTABLE, 
          drSideSwift: "DTCUS"
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.NONREPORTABLE, 
          drSideSwift: "NTNRC"
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1; B and T Section B.1 (C) - page 14, 15", 
          reportable: rep.NONREPORTABLE
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_CURR], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_LOCAL], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B2 - A (i) a and b  (for IN)", 
          reportable: rep.REPORTABLE, 
          category: ["200", "250", "251"], 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at._CASH_], 
          resException: "MUTUAL PARTY"
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_LOCAL], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_CURR], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B2 - A (i) a and b  (for OUT)", 
          reportable: rep.REPORTABLE, 
          category: ["200", "250", "251"], 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at._CASH_], 
          resException: "MUTUAL PARTY"
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_CURR], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B2 - A (i) c, d, e, f, g and h  (for IN)", 
          reportable: rep.REPORTABLE, 
          category: ["200", "252", "255", "256", "530/05"], 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResException: "MUTUAL PARTY", 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_CURR], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B2 - A (i) c, d, e, f, g and h  (for OUT)", 
          reportable: rep.REPORTABLE, 
          category: ["200", "252", "255", "256", "530/05"], 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResException: "MUTUAL PARTY", 
          resSide: drcr.DR, 
          resAccountType: [at._CASH_, at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_CURR], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B2 - A (i) c, d, e, f, g and h  (for IN)", 
          reportable: rep.REPORTABLE, 
          category: ["200", "252", "255", "256", "530/05"], 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResException: "MUTUAL PARTY", 
          resSide: drcr.CR, 
          resAccountType: [at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.LOCAL_ACC], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_CURR], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B2 - A (i) c, d, e, f, g and h  (for OUT)", 
          reportable: rep.REPORTABLE, 
          category: ["200", "252", "255", "256", "530/05"], 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResException: "MUTUAL PARTY", 
          resSide: drcr.DR, 
          resAccountType: [at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        category: ["517"], 
        validFrom: "2013-07-18", 
        decision: {
          accStatusFilter: accStatus.Individual, 
          manualSection: "Manual Section B2 - B (i) and (ii) (for IN) (investment funds)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.RE_FCA], 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        notCategory: ["517"], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1 (non investment funds)", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResException: ["FCA RESIDENT NON REPORTABLE"], 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.FCA], 
        category: ["513"], 
        validFrom: "2013-07-18", 
        decision: {
          accStatusFilter: accStatus.Individual, 
          manualSection: "Manual Section B2 - B (ii) (for OUT) (investment funds)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.RE_FCA], 
          resSide: drcr.DR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.FCA], 
        notCategory: ["513"], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1 (non investment funds)", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResException: ["FCA RESIDENT NON REPORTABLE"], 
          resSide: drcr.DR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.NOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B2 - B (iii) (for OUT)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.RE_FBC], 
          resSide: drcr.DR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.NOSTRO], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B2 - B (iii) (for IN)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.RE_FBC], 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CFC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B2 - C i, ii  (for OUT)", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.CR, 
          nonResException: ["CFC RESIDENT NON REPORTABLE"], 
          resSide: drcr.DR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CFC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B2 - C i, ii  (for IN)", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.DR, 
          nonResException: ["CFC RESIDENT NON REPORTABLE"], 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.FCA], 
        validFrom: "2013-07-18", 
        drDecision: {
          manualSection: "Manual Section B2 - C (or OUT)", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.CR, 
          nonResException: ["FCA RESIDENT NON REPORTABLE"], 
          resSide: drcr.DR, 
          resAccountType: [at.RE_FCA]
        }, 
        crDecision: {
          manualSection: "Manual Section B2 - C (or IN)", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.DR, 
          nonResException: ["FCA RESIDENT NON REPORTABLE"], 
          resSide: drcr.CR, 
          resAccountType: [at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CFC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CFC], 
        validFrom: "2013-07-18", 
        drDecision: {
          manualSection: "Manual Section B2 - C (for OUT)", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.OUT, 
          nonResSide: drcr.CR, 
          nonResException: ["CFC RESIDENT NON REPORTABLE"], 
          resSide: drcr.DR, 
          resAccountType: [at.RE_CFC]
        }, 
        crDecision: {
          manualSection: "Manual Section B2 - C (for IN)", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          nonResSide: drcr.DR, 
          nonResException: ["CFC RESIDENT NON REPORTABLE"], 
          resSide: drcr.CR, 
          resAccountType: [at.RE_CFC]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CFC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B1 - Page 3 of section 1 shows this as a ZZ1", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          category: "513", 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.RE_FCA], 
          resSide: drcr.DR, 
          resAccountType: [at.RE_CFC]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CFC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Manual Section B1 - Page 3 of section 1 shows this as a ZZ1", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          category: "517", 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.RE_FCA], 
          resSide: drcr.CR, 
          resAccountType: [at.RE_CFC]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.FCA, accType.CFC], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at._CASH_, at._CASH_, at.NR_RND], 
          resSide: drcr.DR, 
          resAccountType: [at.RE_FCA, at.RE_CFC]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.NR_FCA], 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.NR_FCA], 
          resSide: drcr.DR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CFC, accType.FCA], 
        validFrom: "2013-07-18", 
        drDecision: {
          manualSection: "B and T Section B.1 (B) - page 3 (reportable on Credit side)", 
          reportable: rep.NONREPORTABLE, 
          drSideSwift: "NTNRC"
        }, 
        crDecision: {
          manualSection: "B and T Section B.1 (B) - page 3", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.NR_FCA], 
          resSide: drcr.CR, 
          resAccountType: [at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CFC, accType.FCA], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (B) - page 3", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.NR_FCA], 
          resSide: drcr.DR, 
          resAccountType: [at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.FCA], 
        validFrom: "2013-08-19", 
        drDecision: {
          manualSection: "Operations Manual Section B.2 - Page 11", 
          reportable: rep.ZZ1REPORTABLE, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.DR, 
          flow: flowDir.OUT, 
          nonResAccountType: [at.NR_FCA], 
          resException: ["FCA NON RESIDENT NON REPORTABLE"]
        }, 
        crDecision: {
          manualSection: "Operations Manual Section B.2 - Page 11", 
          reportable: rep.ZZ1REPORTABLE, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.CR, 
          flow: flowDir.IN, 
          nonResAccountType: [at.NR_FCA], 
          resException: ["FCA NON RESIDENT NON REPORTABLE"]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.NOSTRO], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.NOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.NOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.VOSTRO, accType.CFC, accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.NOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.NOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Std Bank Workshop - Assume Resident Nostro refers to bank's CFC account", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.NR_OTH], 
          resSide: drcr.DR, 
          resAccountType: [at.RE_OTH]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_CURR, accType.CASH_LOCAL, accType.CFC, accType.FCA, accType.LOCAL_ACC, accType.VOSTRO], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.NOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.VOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A, B) - page 1, 3", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.VOSTRO], 
          resException: "FCA NON RESIDENT NON REPORTABLE"
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.VOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A, B) - page 1, 3", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.VOSTRO], 
          resException: "FCA NON RESIDENT NON REPORTABLE"
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.VOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.VOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.VOSTRO], 
          resException: "VOSTRO NON REPORTABLE"
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.NOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.NR_FCA], 
          resException: ["FCA NON RESIDENT NON REPORTABLE"]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.NOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.NR_FCA], 
          resException: ["FCA NON RESIDENT NON REPORTABLE"]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.NOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.VOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.VOSTRO], 
          resException: ["VOSTRO NON REPORTABLE"]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.VOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.NOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (A) - page 1", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.VOSTRO], 
          resException: ["VOSTRO NON REPORTABLE"]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.VOSTRO], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.VOSTRO, accType.NOSTRO, accType.CFC, accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario: Resident Vostro accounts are not valid - use Resident RAND or Non Resident VOSTRO instead", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.VOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.VOSTRO, accType.NOSTRO, accType.CFC, accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario: Resident Vostro accounts are not valid - use Resident RAND or Non Resident VOSTRO instead", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.NOSTRO, accType.CFC, accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.VOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario: Resident Vostro accounts are not valid - use Resident RAND or Non Resident VOSTRO instead", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.VOSTRO, accType.NOSTRO, accType.CFC, accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.VOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario: Resident Vostro accounts are not valid - use Resident RAND or Non Resident VOSTRO instead", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CFC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario: Non Resident CFC accounts are not valid", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CFC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario: Non Resident CFC accounts are not valid", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA, accType.VOSTRO, accType.NOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CFC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario: Non Resident CFC accounts are not valid", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CFC], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.FCA, accType.VOSTRO, accType.NOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Illegal scenario: Non Resident CFC accounts are not valid", 
          reportable: rep.ILLEGAL
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_CURR], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_CURR], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Direct foreign cash exchange between residents is not reportable", 
          reportable: rep.NONREPORTABLE
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_CURR], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Direct foreign cash exchange between residents and non residents is not reportable", 
          reportable: rep.NONREPORTABLE
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_CURR], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Direct foreign cash exchange between residents and non residents is not reportable", 
          reportable: rep.NONREPORTABLE
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_CURR], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_CURR], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Direct foreign cash exchange between non residents is not reportable", 
          reportable: rep.NONREPORTABLE
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.NOSTRO], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.NOSTRO], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "Direct foreign cash exchange between non residents is not reportable", 
          reportable: rep.NONREPORTABLE
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CFC, accType.FCA], 
        validFrom: "2013-07-18", 
        decision: {
          manualSection: "B and T Section B.1 (C) - page 1", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.NR_RND, at.NR_OTH, at.NR_RND], 
          resSide: drcr.CR, 
          resAccountType: [at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.IHQ, 
        drAccType: [accType.CFC, accType.FCA], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.VOSTRO, accType.NOSTRO, accType.FCA], 
        validFrom: "2014-12-19", 
        decision: {
          manualSection: "Manual Section B4 - A i (Cross Border transactions: Outflow; IHQ to NonResident)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          subject: "IHQnnn", 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.NR_RND, at.NR_OTH, at.NR_FCA], 
          resSide: drcr.DR, 
          resAccountType: [at.RE_FCA, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.VOSTRO, accType.NOSTRO, accType.FCA], 
        crResStatus: resStatus.IHQ, 
        crAccType: [accType.CFC, accType.FCA], 
        validFrom: "2014-12-19", 
        decision: {
          manualSection: "Manual Section B4 - A i (Cross Border transactions: Inflow; NonResident to IHQ)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          subject: "IHQnnn", 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.NR_RND, at.NR_OTH, at.NR_FCA], 
          resSide: drcr.CR, 
          resAccountType: [at.RE_FCA, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.IHQ, 
        drAccType: [accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        validFrom: "2014-12-19", 
        decision: {
          manualSection: "Manual Section B4 - A ii (Local transactions: Inflow; IHQ to Resident)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          subject: "IHQnnn", 
          locationCountry: "ZA", 
          nonResException: "IHQ", 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.CASH_CURR, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        crResStatus: resStatus.IHQ, 
        crAccType: [accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        validFrom: "2014-12-19", 
        decision: {
          manualSection: "Manual Section B4 - A ii (Local transactions: Outflow; Resident to IHQ)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          subject: "IHQnnn", 
          locationCountry: "ZA", 
          nonResException: "IHQ", 
          resSide: drcr.DR, 
          resAccountType: [at._CASH_, at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.HOLDCO, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.RESIDENT, 
        crAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        validFrom: "2014-12-19", 
        decision: {
          manualSection: "Manual Section B4 - B i b (HOLDCO FCA to Resident)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          subject: "HOLDCO", 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.NR_FCA], 
          resSide: drcr.CR, 
          resAccountType: [at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.RESIDENT, 
        drAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        crResStatus: resStatus.HOLDCO, 
        crAccType: [accType.FCA], 
        validFrom: "2014-12-19", 
        decision: {
          manualSection: "Manual Section B4 - B i b (Resident to HOLDCO FCA)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          subject: "HOLDCO", 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.NR_FCA], 
          resSide: drcr.DR, 
          resAccountType: [at._CASH_, at.RE_OTH, at.RE_CFC, at.RE_FCA]
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.LOCAL_ACC, accType.CFC, accType.FCA, accType.NOSTRO], 
        crResStatus: resStatus.HOLDCO, 
        crAccType: [accType.FCA], 
        validFrom: "2014-12-19", 
        decision: {
          manualSection: "Manual Section B4 - B i c (Non Resident to HOLDCO FCA)", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.DR, 
          nonResAccountType: [at.NR_RND, at.NR_FCA, at.NR_FCA, at.NR_FCA], 
          resException: "FCA NON RESIDENT NON REPORTABLE"
        }
      },
      {
        drResStatus: resStatus.HOLDCO, 
        drAccType: [accType.FCA], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.LOCAL_ACC, accType.CFC, accType.FCA], 
        validFrom: "2014-12-19", 
        decision: {
          manualSection: "Manual Section B4 - B i c (HOLDCO FCA to Non Resident)", 
          reportable: rep.ZZ1REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          nonResSide: drcr.CR, 
          nonResAccountType: [at.NR_RND, at.NR_FCA, at.NR_FCA], 
          resException: "FCA NON RESIDENT NON REPORTABLE"
        }
      },
      {
        drResStatus: resStatus.HOLDCO, 
        drAccType: [accType.LOCAL_ACC], 
        crResStatus: resStatus.NONRES, 
        crAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC, accType.NOSTRO, accType.VOSTRO, accType.FCA], 
        validFrom: "2014-12-19", 
        decision: {
          manualSection: "Manual Section B4 - B i d (HOLDCO Rand to Non Resident)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.OUT, 
          reportingSide: drcr.DR, 
          subject: "HOLDCO", 
          nonResSide: drcr.CR, 
          nonResAccountType: [at._CASH_, at.NR_RND, at.NR_OTH, at.NR_RND, at.NR_FCA], 
          resException: "NON RESIDENT RAND"
        }
      },
      {
        drResStatus: resStatus.NONRES, 
        drAccType: [accType.CASH_LOCAL, accType.LOCAL_ACC, accType.NOSTRO, accType.VOSTRO, accType.FCA], 
        crResStatus: resStatus.HOLDCO, 
        crAccType: [accType.LOCAL_ACC], 
        validFrom: "2014-12-19", 
        decision: {
          manualSection: "Manual Section B4 - B i d (Non Resident to HOLDCO Rand)", 
          reportable: rep.REPORTABLE, 
          flow: flowDir.IN, 
          reportingSide: drcr.CR, 
          subject: "HOLDCO", 
          nonResSide: drcr.DR, 
          nonResAccountType: [at._CASH_, at.NR_RND, at.NR_OTH, at.NR_RND, at.NR_FCA], 
          resException: "NON RESIDENT RAND"
        }
      }
    ];
    v2 = [
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.LOCAL_ACC, accType.CASH_ZAR, accType.CASH_CURR], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.CFC], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.CFC, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.NOSTRO], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.FCA], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.HOLDCO, accType.FCA, entity.and(localBank.and(transferCURR))),
          use(resStatus.RESIDENT, accType.FCA, individual.and(localBank.and(transferCURR))),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.VOSTRO], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.VOSTRO], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.CASH_CURR, accType.CASH_LOCAL], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.IHQ, 
        accType: [accType.LOCAL_ACC, accType.CASH_ZAR, accType.CASH_CURR], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.IHQ, 
        accType: [accType.CFC], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.CFC, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.IHQ, 
        accType: [accType.FCA], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.HOLDCO, accType.FCA, entity.and(localBank.and(transferCURR))),
          use(resStatus.RESIDENT, accType.FCA, individual.and(localBank.and(transferCURR))),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.NONRES, 
        accType: [accType.LOCAL_ACC], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.NONRES, 
        accType: [accType.CFC, accType.FCA], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.NONRES, 
        accType: [accType.NOSTRO], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.NONRES, 
        accType: [accType.VOSTRO], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.NONRES, 
        accType: [accType.CASH_LOCAL, accType.CASH_CURR], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.HOLDCO, 
        accType: [accType.LOCAL_ACC], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.HOLDCO, 
        accType: [accType.FCA], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.DR, 
        resStatus: resStatus.HOLDCO, 
        accType: [accType.VOSTRO], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        knownSide: drcr.CR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.LOCAL_ACC, accType.FCA], 
        rules: [
          use(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          use(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          use(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          use(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          use(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.CR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.CASH_LOCAL, accType.CASH_CURR], 
        rules: [
          use(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          use(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          use(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          use(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.CR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.VOSTRO], 
        rules: [
          use(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          use(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          use(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          use(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.CR, 
        resStatus: resStatus.NONRES, 
        accType: [accType.LOCAL_ACC, accType.FCA], 
        rules: [
          use(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          use(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          use(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          use(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.CR, 
        resStatus: resStatus.NONRES, 
        accType: [accType.CASH_LOCAL, accType.CASH_CURR], 
        rules: [
          use(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          use(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          use(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          use(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.CR, 
        resStatus: resStatus.HOLDCO, 
        accType: [accType.LOCAL_ACC, accType.FCA], 
        rules: [
          use(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          use(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          use(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          use(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.CR, 
        resStatus: resStatus.RESIDENT, 
        accType: [accType.CFC, accType.NOSTRO], 
        rules: [
          use(resStatus.NONRES, accType.FCA, localBankOther.and(transferCURR).and(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS")))),
          use(resStatus.RESIDENT, accType.CFC, localBankOther.and(transferCURR).and(not(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS"))))),
          use(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC").or(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS"))))),
          use(resStatus.RESIDENT, accType.CFC, localBankAD.and(transferCURR).and(not(field72("NTNRC").or(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS")))))),
          use(resStatus.NONRES, accType.LOCAL_ACC, localBankOther.and(transferLOCAL).and(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS")))),
          use(resStatus.RESIDENT, accType.CFC, localBankOther.and(transferLOCAL).and(not(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS"))))),
          use(resStatus.RESIDENT, accType.CFC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          use(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.CR, 
        resStatus: resStatus.NONRES, 
        accType: [accType.CFC, accType.NOSTRO], 
        rules: [
          use(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          use(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          use(resStatus.RESIDENT, accType.CFC, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          use(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        knownSide: drcr.CR, 
        resStatus: resStatus.NONRES, 
        accType: [accType.VOSTRO], 
        rules: [
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          use(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          use(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          use(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRB")))),
          use(resStatus.NONRES, accType.VOSTRO, localBankAD.and(field72("NTNRB"))),
          use(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      }
    ];
    v3 = [
      {
        scenario: "MissingBICData", 
        match: not(drHasValue("BankBIC")).or(drHasValue("BankBIC", "=", "")).or(not(crHasValue("BankBIC"))).or(crHasValue("BankBIC", "=", "")), 
        rules: [
          decide({
            manualSection: "Both a dr Bank BIC as well as a cr Bank BIC must always be provided", 
            reportable: rep.ILLEGAL
          })
        ]
      },
      {
        scenario: "OtherBank", 
        match: not(drThisBank).and(not(crThisBank)), 
        rules: [
          decide({
            manualSection: "Payments made where our bank is just a correspondent bank and both parties are not with us, therfore payment is not reportable", 
            reportable: rep.NONREPORTABLE
          })
        ]
      },
      {
        scenario: "BankPayment", 
        match: drThisBank.and(not(crThisBank)).and(drNoResStatus).and(drHasAccType([accType.NOSTRO])), 
        rules: [
          decideCr({
            manualSection: "All interbank transactions made to foreign banks or foreign currency transactions are reportable", 
            reportable: rep.ZZ1REPORTABLE, 
            flow: flowDir.OUT, 
            reportingSide: drcr.DR, 
            resSide: drcr.DR, 
            resAccountType: at.RE_OTH, 
            nonResSide: drcr.CR, 
            nonResAccountType: at.NR_OTH
          }, offshoreBank.or(transferCURR)),
          decideCr({
            manualSection: "Banking payments made to local banks in local currencies are not reportable", 
            reportable: rep.NONREPORTABLE
          }, localBank.and(transferLOCAL))
        ]
      },
      {
        scenario: "BankReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crNoResStatus).and(crHasAccType([accType.NOSTRO])), 
        rules: [
          decideDr({
            manualSection: "All interbank transactions received from foreign banks or in foreign currency are reportable", 
            reportable: rep.ZZ1REPORTABLE, 
            flow: flowDir.IN, 
            reportingSide: drcr.CR, 
            resSide: drcr.CR, 
            resAccountType: at.RE_OTH, 
            nonResSide: drcr.DR, 
            nonResAccountType: at.NR_OTH
          }, offshoreBank.or(transferCURR)),
          decideDr({
            manualSection: "Banking payments received from local banks in local currencies are not reportable", 
            reportable: rep.NONREPORTABLE
          }, localBank.and(transferLOCAL))
        ]
      },
      {
        scenario: "InwardIntermediaryBank", 
        match: not(drThisBank).and(not(crThisBank)).and(not(drLocalBank)).and(drHasAccType([accType.NOSTRO, accType.VOSTRO])).and(crHasAccType([accType.NOSTRO, accType.VOSTRO])), 
        rules: [
          decideCr({
            manualSection: "Offshore Bank to Local Bank (via us). Instruct beneficiary bank to report appropriately based on type of customer in field 72", 
            reportable: rep.ZZ1REPORTABLE, 
            flow: flowDir.IN, 
            reportingSide: drcr.DR, 
            resSide: drcr.CR, 
            resAccountType: at.RE_OTH, 
            nonResSide: drcr.DR, 
            nonResAccountType: at.NR_OTH
          }, localBank),
          decideCr({
            manualSection: "Offshore Bank to Offshore Bank (via us). Nothing to report", 
            reportable: rep.NONREPORTABLE
          }, offshoreBank)
        ]
      },
      {
        scenario: "OutwardIntermediaryBank", 
        match: not(drThisBank).and(not(crThisBank)).and(drLocalBank).and(drHasAccType([accType.NOSTRO, accType.VOSTRO])).and(crHasAccType([accType.NOSTRO, accType.VOSTRO])), 
        rules: [
          decideCr({
            manualSection: "Local Bank to offshore. Ordering institution is required to do any of the reporting", 
            reportable: rep.NONREPORTABLE
          }, offshoreBank),
          decideCr({
            manualSection: "Local Bank to local Bank requires no reporting", 
            reportable: rep.NONREPORTABLE
          }, localBank)
        ]
      },
      {
        scenario: "CustomerOnUs", 
        match: drThisBank.and(crThisBank).and(drHasResStatus([resStatus.RESIDENT, resStatus.NONRES, resStatus.HOLDCO, resStatus.IHQ])).and(crHasResStatus([resStatus.RESIDENT, resStatus.NONRES, resStatus.HOLDCO, resStatus.IHQ])), 
        rules: [
          useBoth()
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.RESIDENT)).and(drHasAccType([accType.LOCAL_ACC, accType.CASH_LOCAL, accType.CASH_CURR])).and(crHasAccType(accType.CFC)), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.CFC, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.RESIDENT)).and(drHasAccType([accType.LOCAL_ACC, accType.CASH_LOCAL, accType.CASH_CURR])).and(crHasAccType(accType.NOSTRO)), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          decideCr({
            manualSection: "Manual Section B2 - C i, ii  (for OUT). Resident Local Account paying foreign currency to local bank (to a NOSTRO account). This bank is doing the currency conversion so must report", 
            reportable: rep.ZZ1REPORTABLE, 
            flow: flowDir.OUT, 
            reportingSide: drcr.DR, 
            resSide: drcr.DR, 
            resAccountType: at.RE_OTH, 
            nonResException: "NOSTRO NON REPORTABLE"
          }, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.RESIDENT)).and(drHasAccType([accType.LOCAL_ACC, accType.CASH_LOCAL, accType.CASH_CURR])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.NONRES, accType.FCA, localBank.and(transferCURR).and(hasResStatus(resStatus.NONRES))),
          useCr(resStatus.RESIDENT, accType.CFC, localBank.and(transferCURR).and(not(hasResStatus(resStatus.NONRES))).and(entity)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR).and(not(hasResStatus(resStatus.NONRES)))),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.RESIDENT)).and(drHasAccType([accType.CFC])).and(crHasAccType([accType.FCA])), 
        rules: [
          decideCr({
            manualSection: "Manual Section B2 - C i, ii  (for OUT). Resident CFC paying ZAR to local bank. This bank is doing the currency conversion so must report", 
            reportable: rep.ZZ1REPORTABLE, 
            flow: flowDir.OUT, 
            reportingSide: drcr.DR, 
            resSide: drcr.DR, 
            resAccountType: at.RE_CFC, 
            nonResException: "FCA RESIDENT NON REPORTABLE"
          }, localBank.and(transferLOCAL)),
          useCr(resStatus.NONRES, accType.FCA, localBank.and(transferCURR).and(hasResStatus(resStatus.NONRES))),
          decideCr({
            manualSection: "Manual Section B2 - C i, ii  (for OUT). Resident CFC paying currency to local bank. We need to report Dr leg for CFC account", 
            reportable: rep.ZZ1REPORTABLE, 
            flow: flowDir.OUT, 
            reportingSide: drcr.DR, 
            resSide: drcr.DR, 
            resAccountType: at.RE_CFC, 
            nonResException: "FCA RESIDENT NON REPORTABLE"
          }, localBank.and(transferCURR).and(not(hasResStatus(resStatus.NONRES)))),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.RESIDENT)).and(drHasAccType([accType.CFC])), 
        rules: [
          decideCr({
            manualSection: "Manual Section B2 - C i, ii  (for IN). Resident CFC paying ZAR to local bank. This bank is doing the currency conversion so must report", 
            reportable: rep.ZZ1REPORTABLE, 
            flow: flowDir.OUT, 
            reportingSide: drcr.DR, 
            resSide: drcr.DR, 
            resAccountType: at.RE_CFC, 
            nonResException: "CFC RESIDENT NON REPORTABLE"
          }, localBank.and(transferLOCAL)),
          useCr(resStatus.NONRES, accType.FCA, localBank.and(transferCURR).and(hasResStatus(resStatus.NONRES))),
          useCr(resStatus.RESIDENT, accType.CFC, localBank.and(transferCURR).and(not(hasResStatus(resStatus.NONRES)))),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.RESIDENT)).and(drHasAccType([accType.NOSTRO])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.RESIDENT)).and(drHasAccType([accType.FCA])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.HOLDCO, accType.FCA, entity.and(localBank.and(transferCURR))),
          useCr(resStatus.RESIDENT, accType.FCA, individual.and(localBank.and(transferCURR))),
          useCr(resStatus.NONRES, accType.FCA, localBank.and(transferCURR).and(hasResStatus(resStatus.NONRES))),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR).and(not(hasResStatus(resStatus.NONRES)))),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.RESIDENT)).and(drHasAccType([accType.VOSTRO])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.RESIDENT)).and(drHasAccType([accType.CASH_CURR, accType.CASH_LOCAL])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.IHQ)).and(drHasAccType([accType.LOCAL_ACC, accType.CASH_ZAR, accType.CASH_CURR])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.IHQ)).and(drHasAccType([accType.CFC])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.CFC, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.IHQ)).and(drHasAccType([accType.FCA])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.HOLDCO, accType.FCA, entity.and(localBank.and(transferCURR))),
          useCr(resStatus.RESIDENT, accType.FCA, individual.and(localBank.and(transferCURR))),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.NONRES)).and(drHasAccType([accType.LOCAL_ACC])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.NONRES)).and(drHasAccType([accType.CFC, accType.FCA])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.NONRES)).and(drHasAccType([accType.NOSTRO])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.NONRES)).and(drHasAccType([accType.VOSTRO])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.NONRES)).and(drHasAccType([accType.CASH_LOCAL, accType.CASH_CURR])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.HOLDCO)).and(drHasAccType([accType.LOCAL_ACC])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.HOLDCO)).and(drHasAccType([accType.FCA])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerPayment", 
        match: drThisBank.and(not(crThisBank)).and(drHasResStatus(resStatus.HOLDCO)).and(drHasAccType([accType.VOSTRO])), 
        rules: [
          useCr(resStatus.RESIDENT, accType.LOCAL_ACC, localBank.and(transferLOCAL)),
          useCr(resStatus.RESIDENT, accType.FCA, localBank.and(transferCURR)),
          useCr(resStatus.NONRES, accType.NOSTRO, offshoreBank.and(transferLOCAL.or(transferCURR)))
        ]
      },
      {
        scenario: "CustomerReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crHasResStatus(resStatus.RESIDENT)).and(crHasAccType([accType.LOCAL_ACC])), 
        rules: [
          useDr(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          useDr(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          useDr(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB").and(field72("NTNRC"))))),
          useDr(resStatus.NONRES, accType.FCA, localBankAD.and(transferLOCAL).and(field72("NTNRC"))),
          useDr(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          useDr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crHasResStatus(resStatus.RESIDENT)).and(crHasAccType([accType.FCA])), 
        rules: [
          useDr(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          useDr(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          useDr(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          decideDr({
            accStatusFilter: accStatus.Individual, 
            manualSection: "Manual Section B2 - B (ii) (for OUT) (investment funds)", 
            reportable: rep.REPORTABLE, 
            flow: flowDir.OUT, 
            reportingSide: drcr.CR, 
            nonResSide: drcr.CR, 
            nonResAccountType: at.RE_FCA, 
            resSide: drcr.DR, 
            resAccountType: at.RE_OTH
          }, localBankOther.and(transferLOCAL)),
          useDr(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          useDr(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          useDr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crHasResStatus(resStatus.RESIDENT)).and(crHasAccType([accType.CASH_LOCAL, accType.CASH_CURR])), 
        rules: [
          useDr(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          useDr(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          useDr(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          useDr(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          useDr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crHasResStatus(resStatus.RESIDENT)).and(crHasAccType([accType.VOSTRO])), 
        rules: [
          useDr(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          useDr(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          useDr(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          useDr(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          useDr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crHasResStatus(resStatus.NONRES)).and(crHasAccType([accType.LOCAL_ACC, accType.FCA])), 
        rules: [
          useDr(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          useDr(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          useDr(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          useDr(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          useDr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crHasResStatus(resStatus.NONRES)).and(crHasAccType([accType.CASH_LOCAL, accType.CASH_CURR])), 
        rules: [
          useDr(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          useDr(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          useDr(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          useDr(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          useDr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crHasResStatus(resStatus.HOLDCO)).and(crHasAccType([accType.LOCAL_ACC, accType.FCA])), 
        rules: [
          useDr(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          useDr(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          useDr(resStatus.RESIDENT, accType.FCA, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          useDr(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          useDr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crHasResStatus(resStatus.RESIDENT)).and(crHasAccType([accType.CFC, accType.NOSTRO])), 
        rules: [
          useDr(resStatus.NONRES, accType.FCA, localBankOther.and(transferCURR).and(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS")))),
          useDr(resStatus.RESIDENT, accType.CFC, localBankOther.and(transferCURR).and(not(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS"))))),
          useDr(resStatus.NONRES, accType.FCA, localBankAD.and(transferCURR).and(field72("NTNRC").or(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS"))))),
          useDr(resStatus.RESIDENT, accType.CFC, localBankAD.and(transferCURR).and(not(field72("NTNRC").or(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS")))))),
          useDr(resStatus.NONRES, accType.LOCAL_ACC, localBankOther.and(transferLOCAL).and(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS")))),
          useDr(resStatus.RESIDENT, accType.CFC, localBankOther.and(transferLOCAL).and(not(field72("TRANSFER FROM ABROAD").or(field72("RETURN OF FUNDS"))))),
          useDr(resStatus.RESIDENT, accType.CFC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          useDr(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          useDr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crHasResStatus(resStatus.NONRES)).and(crHasAccType([accType.CFC, accType.NOSTRO])), 
        rules: [
          useDr(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          useDr(resStatus.NONRES, accType.CFC, localBankAD.and(transferCURR).and(field72("NTNRC"))),
          useDr(resStatus.RESIDENT, accType.CFC, localBankAD.and(transferCURR).and(not(field72("NTNRC")))),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          useDr(resStatus.NONRES, accType.VOSTRO, localBankAD.and(transferLOCAL).and(field72("NTNRB"))),
          useDr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      },
      {
        scenario: "CustomerReceipt", 
        match: crThisBank.and(not(drThisBank)).and(crHasResStatus(resStatus.NONRES)).and(crHasAccType([accType.VOSTRO])), 
        rules: [
          useDr(resStatus.RESIDENT, accType.LOCAL_ACC, localBankOther.and(transferLOCAL)),
          useDr(resStatus.RESIDENT, accType.FCA, localBankOther.and(transferCURR)),
          decideDr({
            manualSection: "B and T Section B.1 (A and B) - pages 1, 2, 3", 
            reportable: rep.REPORTABLE, 
            flow: flowDir.OUT, 
            reportingSide: drcr.CR, 
            nonResSide: drcr.CR, 
            nonResAccountType: at.VOSTRO, 
            resSide: drcr.DR, 
            resAccountType: at.RE_OTH
          }, localBankAD.and(transferLOCAL).and(not(field72("NTNRB")))),
          decideDr({
            manualSection: "B and T Section B.1 (A and B) - pages 1, 2, 3", 
            reportable: rep.REPORTABLE, 
            flow: flowDir.OUT, 
            reportingSide: drcr.CR, 
            nonResSide: drcr.CR, 
            nonResAccountType: at.VOSTRO, 
            resSide: drcr.DR, 
            resAccountType: at.RE_FCA
          }, localBankAD.and(transferCURR).and(not(field72("NTNRB")))),
          useDr(resStatus.NONRES, accType.VOSTRO, localBankAD.and(field72("NTNRB"))),
          useDr(resStatus.NONRES, accType.NOSTRO, offshoreBank)
        ]
      }
    ];
  }
  return {
    comment: 'sbZABOLP (1,0) -> sbZAFlow (1,0) -> stdSARB (1,0) -> coreSARBExternal (1,0) -> coreSARB (1,0) -> coreSADC (1,0)',
    engine: {major: '1', minor: '0'},
    context: ctx,
    rules: [v1],
    assumptions: [v2],
    scenarios: [v3],
    mappings: {Locale: "ZA", LocalCurrencySymbol: "R", RegulatorPrefix: "CB", StateName: "Province", DealerPrefix: "RE", LocalValue: "DomesticValue", LocalCurrency: "ZAR", Regulator: "Regulator", LocalCurrencyName: "Local Currency", _maxLenErrorType: "ERROR", _lenErrorType: "ERROR", _minLenErrorType: "ERROR"}
  };
}})
