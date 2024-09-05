<html>
<div style='border-width: 0px; padding: 20px; border-style: solid; border-color: #999999; width: fit-content'>
  <div>
    <div style='background:#e3e3e3; font-family:georgia,garamond,serif;font-size:16px; padding:5px 15px'>
      <p>Hi All,</p>
      <p> Please find below automated test execution report.</p>
      <p> For more details such as stacktrace, screenshots please refer to <a href='${allureReportUrl}'>Allure Report</a></p>
    </div>
    <div style='padding:15px 0px 0px;'>
      <table
          style='font-family: verdana,arial,sans-serif; font-size:11px; color:#333333; border-width: 1px; border-color: #999999; border-collapse: collapse; width:100%'>
        <thead>
        <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>Project</th>
        <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>Build Id</th>
        <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>Date</th>
        <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>Environment</th>
        </thead>
        <tbody>
        <tr>
          <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>${projectName}</td>
          <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>${buildId}</td>
          <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;' align=center>${date}</td>
          <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;' align=center>${environment}</td>
        </tr>
        </tbody>
      </table>
    </div>
    <div>
      <h1 style='font-family:georgia,garamond,serif;font-size:16px;font-style:italic;'><b>${summaryTableHeading}</b></h1>
      <table
          style='font-family: verdana,arial,sans-serif; font-size:11px; color:#333333; border-width: 1px; border-color: #999999; border-collapse: collapse; width:100%'>
        <tbody>
        <tr>
          <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              rowspan='2'><b>Sr No</b></th>
          <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              rowspan='2'><b>Feature</b></th>
          <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              rowspan='2'><b>Status</b></th>
          <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              colspan='3'><b>Scenarios</b></th>
        </tr>
        <tr>
          <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
            <b>Total</b></th>
          <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
            <b>Passed</b></th>
          <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
            <b>Failed</b></th>
        </tr>
        <#list summaryTable as summaryRow>
        <tr>
          <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              align=center>${summaryRow.srNo}
          </td>
          <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              align=left>${summaryRow.featureName}
          </td>
          <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              align=left>${summaryRow.featureStatus}
          </td>
          <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              align=center>${summaryRow.totalScenarios}
          </td>
          <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              align=center>${summaryRow.totalPassed}
          </td>
          <#if summaryRow.totalFailed gt 0 >
          <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999; background-color:#e97474;'
              align=center>${summaryRow.totalFailed} <#else>
          <td style='background:#dcddc0; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              align=center>${summaryRow.totalFailed}
          </td>
        </#if>
        </tr>
        </#list>
        <tr>
          <td style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              align=center colspan=3><b>Total</td>
          <td style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              align=center><b>${allScenarioCount}</b></td>
          <td style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              align=center><b>${allPassedScenarioCount}</b></td>
          <td style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
              align=center><b>${allFailedScenarioCount}</b></td>
        </tr>
        </tbody> </table>
    </div>
    <#if showDetailedStatusTable == "true" >
    <div>
      <h3 style='font-family:georgia,garamond,serif;font-size:16px;font-style:italic;'><b>${detailedStatusTableHeading}</b>
      </h3>
      <table
          style='font-family: verdana,arial,sans-serif; font-size:11px; color:#333333; border-width: 1px; border-color: #999999; border-collapse: collapse; width:100%'>
        <tbody>
          <tr>
            <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
              <b>Feature<br>Sr No</b></th>
            <th style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
              <b>Feature Details</b></th>
          </tr>
          <#list detailedStatusTable as detailedStatusRow>
            <tr>
              <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
                  align=center>${detailedStatusRow.srNo}
              </td>
              <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                <table
                      style='font-family: verdana,arial,sans-serif; font-size:11px; color:#333333; border-width: 1px; border-color: #999999; border-collapse: collapse; width:100%'>
                    <#if showFeatureMetaData == "true" >
                    <tr>
                      <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
                          colspan='7' align=left>
                        <p><b>Feature : ${detailedStatusRow.featureName}</b></p>
                        <p>Feature Description : ${detailedStatusRow.featureDescription}</p>
                        <p>Feature File Path : ${detailedStatusRow.featureFilePath}</p>
                        <p>Feature Tags : ${detailedStatusRow.featureTags}</p></td>
                    </tr>
                    </#if>
                    <tr>
                      <th rowspan="2" style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                        <b>Sr No</b></th>
                      <th rowspan="2" style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999; width: 100%'>
                        <b>Scenario</b></th>
                      <th rowspan="2" style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                        <b>Status</b></th>
                      <th colspan="3" style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                        <b>Steps</b></th>
                      <th rowspan="2" style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                        <b>Comments</b></th>
                    </tr>
                    <tr>
                      <td style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                        <b>Passed</b></td>
                      <td style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                        <b>Failed</b></td>
                      <td style='color:white; background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                        <b>Undefined</b></td>
                    </tr>
                    <#list detailedStatusRow.scenarioTableModels as scenarioTableRow>
                      <tr>
                        <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
                            align=center>${scenarioTableRow.srNo}
                        </td>
                        <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999; width: 100%'>
                          ${scenarioTableRow.scenarioName}
                        </td>
                       <#if scenarioTableRow.status =="Failed">
                        <td style='background-color:#e97474;border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                          ${scenarioTableRow.status}
                        </td>
                        <#else>
                        <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
                            align=center>${scenarioTableRow.status}
                        </td>
                      </#if>
                        <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999; text-align:center'>
                          <b>${scenarioTableRow.stepsPassed}</b></td>
                        <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999; text-align:center'>
                          <b>${scenarioTableRow.stepsFailed}</b></td>
                        <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999; text-align:center'>
                          <b>${scenarioTableRow.stepsUndefined}</b></td>
                        <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                          ${scenarioTableRow.potentialFailureReason}
                        </td>
                      </tr>
                  </#list>
                </table>
              </td>
            </tr>
        </#list>
      </tbody>
    </table>
  </div>
  </#if>
  <div>&nbsp;</div>
  <div>
    <table
        style='font-family: verdana,arial,sans-serif; font-size:11px; color:#333333; border-width: 1px; border-color: #999999; border-collapse: collapse'>
      <tbody>
      <tr style='background:#17a2b8; border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
        <td style='background:#17a2b8;border-width: 1px; padding: 8px; border-style: solid; border-color: #999999; border-bottom: hidden' align=left>
          <p><b>Regards</p>
          <p><b>Team Automation</p>
        </td>
      </tr>
      <tr style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
        <td style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'
            align=left colspan='2'>
          <div>
            <table
                style='font-family: verdana,arial,sans-serif; font-size:11px; color:#333333; border-width: 1px; border-color: #999999; border-collapse: collapse;'>
              <tbody>
              <tr>
                <th style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                  <p align=left><a
                      href='https://confluence.tools.dummycompany.com/pages/viewpage.action?pageId=544017972'>Release
                    Notes</a></p></th>
                <th style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                  <p align=left><a
                      href='https://confluence.tools.dummycompany.com/pages/viewpage.action?pageId=544017972'>FAQs</a>
                  </p></th>
                <th style='border-width: 1px; padding: 8px; border-style: solid; border-color: #999999;'>
                  <p align=left><a
                      href='https://confluence.tools.dummycompany.com/pages/viewpage.action?pageId=544017972'>Automation Support</a>
                  </p></th>
              </tr>
              </tbody>
            </table>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</html>