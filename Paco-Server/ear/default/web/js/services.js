pacoApp.service('experimentService', ['$http', '$cacheFactory', 'util',
  function($http, $cacheFactory, util) {

  var cache = $cacheFactory.get('$http');

  return ({
    getJoined: getJoined,
    getAdministered: getAdministered,
    getJoinable: getJoinable,
    getExperiment: getExperiment,
    joinExperiment: joinExperiment,
    saveExperiment: saveExperiment,
    deleteExperiment: deleteExperiment
  });

  function getData() {}

  function getJoined(reload) {
    if (reload !== undefined && reload === true) {
        cache.remove('/experiments?joined');
    }
    return $http.get('/experiments?joined', {cache: true});
  }

  function getAdministered() {
    return $http.get('/experiments?admin', {cache: true});
  }

  function getJoinable() {
    return $http.get('/experiments?mine', {cache: true});
  }

  function getExperiment(id) {
    return $http.get('/experiments?id=' + id, {cache: true});
  }

  function joinExperiment(experiment) {
    var obj = {};
    obj.experimentId = exp.id;
    obj.appId = 'webform';
    obj.experimentVersion = exp.version;
    obj.experimentName = exp.title;
    obj.responses = [{"name":"joined", "answer": true}];
    obj.responseTime = util.formatDate(new Date());
    var json = JSON.stringify(obj);

    return $http.post('/events', json);
  }

  function saveExperiment(experiment) {

    // Need to clear all list caches in case title was changed
    cache.remove('/experiments?admin');
    cache.remove('/experiments?joined');
    cache.remove('/experiments?joinable');

    // If it's not a new experiment, clear old cached definition
    if (experiment.id) {
      cache.remove('/experiments?id=' + experiment.id);
    }

    return $http.post('/experiments', experiment);
  }

  function deleteExperiment(id) {
    return $http.post('/experiments?delete=1&id=' + id);
  }

}]);


pacoApp.service('config', function() {

  this.tabs = [
    'basics',
    'groups',
    'admin',
    'source',
    'preview'
  ];

  this.dataDeclarations = {
    1: 'App Usage and Browser History',
    2: 'Location Information',
    3: 'Phone Details (Make, Model, Carrier)'
  };

  this.ringtones = [
    'Paco Bark',
    'Paco Alternate Alert Tone'
  ];

  this.scheduleTypes = [
    'Daily',
    'Weekdays',
    'Weekly',
    'Monthly',
    'Random sampling (ESM)'
  ];

  this.actionTypes = [
    'Create notification to participate',
    'Create notification message',
    'Log data',
    'Execute script'
  ];

  this.cueTypes = [
    'HANGUP (deprecated)',
    'USER_PRESENT',
    'Paco action',
    'App Started',
    'App Stopped',
    'Music Started',
    'Music Stopped',
    'Incoming call started',
    'Incoming call ended',
    'Outgoing call started',
    'Outgoing call ended',
    'Missed call',
    'Call Started (in or out)',
    'Call Ended (in or out)',
    "Experiment joined",
    "Experiment ended",
    "Response received"
  ];

  this.esmPeriods = [
    'day',
    'week',
    'month'
  ];

  this.weeksOfMonth = [
    'First',
    'Second',
    'Third',
    'Fourth',
    'Fifth'
  ];

  this.responseTypes = [
    'likert',
    'likert_smileys',
    'open text',
    'list',
    'photo',
    'location'
  ];

  this.feedbackTypes = [
    'Static Message',
    'Retrospective (QS default)',
    'Responsive (adaptive)',
    'Custom Code',
    'Disable Feedback'
  ];
});


pacoApp.service('template', function() {

  this.group = {
    actionTriggers: [],
    name: 'New Group',
    inputs: [],
    feedbackType: 0,
    feedback: {
      type: 0,
      text: 'Thanks for Participating!',
    },
    fixedDuration: 'false'
  };

  this.experiment = {
    admins: [],
    creator: '',
    contactEmail: '',
    extraDataCollectionDeclarations: [],
    published: false,
    publishedUsers: [],
    groups: [this.group],
    dataDeclarations: [],
    ringtoneUri: '/assets/ringtone/Paco Bark'
  }

  this.input = {
    likertSteps: 5,
    responseType: 'open text'
  }

  this.otherAction = {
    type: 'pacoActionAllOthers'
  };

  this.defaultAction = {
    type: 'pacoNotificationAction',
    timeout: 15
  };

  this.schedule = {
    userEditable: true,
    timeout: 15,
    repeatRate: 1
  };

  this.defaultEsmSchedule = {
    esmFrequency: 8,
    esmPeriodInDays: 0,
    esmEndHour: 61200000,
    esmStartHour: 32400000,
    esmWeekends: true,
    minimumBuffer: 59,
    repeatRate: 1,
    scheduleType: 4,
    timeout: 15,
    userEditable: true
  };

  this.cue = {};

  this.scheduleTrigger = {
    type: 'scheduleTrigger',
    actions: [this.defaultAction],
    schedules: [this.schedule]
  };

  this.eventTrigger = {
    type: 'interruptTrigger',
    actions: [this.defaultAction],
    cues: [this.cue],
    minimumBuffer: 59
  };

  this.signalTime = {
    fixedTimeMillisFromMidnight: 0,
    type: 0
  };
});


pacoApp.service('util', ['$filter', function($filter) {

  this.formatDate = function(date) {
    return $filter('date')(date, 'yyyy/MM/dd HH:mm:ssZ');
  };

}]);
