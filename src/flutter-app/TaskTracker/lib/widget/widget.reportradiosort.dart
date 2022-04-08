
import 'package:TaskTracker/service/service.report.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../translator.dart';

class ReportRadioSort extends StatefulWidget {

  final void Function(ReportSortType sortType) sortChangedCallBack;

  final bool isTeam;

  const ReportRadioSort({Key key, this.sortChangedCallBack, this.isTeam}) : super(key: key);

  @override
  _ReportRadioSortState createState() => _ReportRadioSortState();

}

class _ReportRadioSortState extends State<ReportRadioSort> {

  ReportSortType _sortBy;

  void _onSortChanged(ReportSortType value) {
    setState(() {
      _sortBy = value;
      widget.sortChangedCallBack(_sortBy);
    });
  }

  Widget getSizedRadioButton(String name, final ReportSortType type) {
    return Expanded(
      child: ListTile(
        title: Text(name),
        leading: Radio<ReportSortType>(
            value: type,
            groupValue: _sortBy,
            onChanged: _onSortChanged
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    var radioButtons = widget.isTeam ? <Widget>[
      getSizedRadioButton(Translator.text('Common', 'Team'), ReportSortType.REPORT_SORT_TYPE_TEAM),
      getSizedRadioButton(Translator.text('Common', 'User'), ReportSortType.REPORT_SORT_TYPE_USER),
      getSizedRadioButton(Translator.text('Common', 'Week'), ReportSortType.REPORT_SORT_TYPE_WEEK),
      getSizedRadioButton(Translator.text('Common', 'Task'), ReportSortType.REPORT_SORT_TYPE_TASK)]
        :
    <Widget>[
      getSizedRadioButton(Translator.text('Common', 'Week'), ReportSortType.REPORT_SORT_TYPE_WEEK),
      getSizedRadioButton(Translator.text('Common', 'Task'), ReportSortType.REPORT_SORT_TYPE_TASK)];
    return
      Column(
        children: <Widget>[
          Row(
            children: <Widget>[
              Text(Translator.text('WidgetReport', 'Sort Report By'),
                textAlign: TextAlign.left,
                style: TextStyle(fontWeight: FontWeight.w400, fontSize: 18),
              )
            ]
          ),
          SizedBox(height: 20),
          Row(
            children: radioButtons
          ),
        ],
      );
  }
}