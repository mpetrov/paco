/* Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "PacoLocation.h"

#import <CoreLocation/CoreLocation.h>
#import "PacoDate.h"


NSTimer* LocationTimer;

@interface PacoLocation () <CLLocationManagerDelegate>
@property (nonatomic, copy, readwrite) CLLocation *location;
@property (nonatomic, retain) CLLocationManager *manager;
@property (nonatomic, assign) NSInteger numUpdates;
@end

@implementation PacoLocation


- (id)init {
  self = [super init];
  if (self) {
    self.manager = [[CLLocationManager alloc] init];
    self.manager.delegate = self;
    self.numUpdates = 0;
    
    // to save battery life make the accuracy very low
    [self.manager setDesiredAccuracy:kCLLocationAccuracyThreeKilometers];
    
    dispatch_async(dispatch_get_main_queue(), ^{
      LocationTimer = [NSTimer scheduledTimerWithTimeInterval:59.0
                                                       target:self
                                                     selector:@selector(LocationTimerHandler:)
                                                     userInfo:nil
                                                      repeats:YES];
    });
  }
  
  return self;
}

-(void)LocationTimerHandler:(NSTimer *) LocationTimer {
  NSLog(@"Paco LocationTimer fired @ %@", [PacoDate pacoStringForDate:[LocationTimer fireDate]]);

  // Notify our PacoClient that our timer fired
  if ([self.delegate respondsToSelector:@selector(timerUpdated)]) {
    [_delegate timerUpdated];
  }
}

- (void)enableLocationTimer {
  NSLog(@"Paco background LocationTimer got enabled");
  [self.manager startUpdatingLocation];
}

- (void)disableLocationTimer {
  NSLog(@"Paco background LocationTimer got disabled");
  [self.manager stopUpdatingLocation];
}

- (void)updateLocation {
  self.numUpdates = 0;
  [self.manager startUpdatingLocation];
}

#pragma mark - CLLocationManagerDelegate

/*
 *  locationManager:didUpdateToLocation:fromLocation:
 *
 *  Discussion:
 *    Invoked when a new location is available. oldLocation may be nil if there is no previous location
 *    available.
 *
 *    This method is deprecated. If locationManager:didUpdateLocations: is
 *    implemented, this method will not be called.
 */
- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation {
  
  //CLLocationCoordinate2D coord = newLocation.coordinate;
  
  self.location = newLocation;
  self.numUpdates = self.numUpdates + 1;
  if (self.numUpdates > 3) {
// TODO TPE: temporary disabled since the logic for the location is going to change 
//    [self.manager stopUpdatingLocation];
    if ([self.delegate respondsToSelector:@selector(locationUpdated:)]) {
      [_delegate locationUpdated:self.location];
    }
  }
}

/*
 *  locationManager:didFailWithError:
 *
 *  Discussion:
 *    Invoked when an error has occurred. Error types are defined in "CLError.h".
 */
- (void)locationManager:(CLLocationManager *)manager
       didFailWithError:(NSError *)error {
  NSLog(@"Unable to start location manager. Error:%@", [error description]);
}

#if 0
/*
 *  locationManager:didUpdateLocations:
 *
 *  Discussion:
 *    Invoked when new locations are available.  Required for delivery of
 *    deferred locations.  If implemented, updates will
 *    not be delivered to locationManager:didUpdateToLocation:fromLocation:
 *
 *    locations is an array of CLLocation objects in chronological order.
 */
- (void)locationManager:(CLLocationManager *)manager
didUpdateLocations:(NSArray *)locations __OSX_AVAILABLE_STARTING(__MAC_NA,__IPHONE_6_0) {
  NSLog(@"Event: Low Energy didUpdateLocations");
}

/*
 *  locationManager:didUpdateHeading:
 *
 *  Discussion:
 *    Invoked when a new heading is available.
 */
- (void)locationManager:(CLLocationManager *)manager
didUpdateHeading:(CLHeading *)newHeading __OSX_AVAILABLE_STARTING(__MAC_NA,__IPHONE_3_0);

/*
 *  locationManagerShouldDisplayHeadingCalibration:
 *
 *  Discussion:
 *    Invoked when a new heading is available. Return YES to display heading calibration info. The display
 *    will remain until heading is calibrated, unless dismissed early via dismissHeadingCalibrationDisplay.
 */
- (BOOL)locationManagerShouldDisplayHeadingCalibration:(CLLocationManager *)manager  __OSX_AVAILABLE_STARTING(__MAC_NA,__IPHONE_3_0);

/*
 *  locationManager:didEnterRegion:
 *
 *  Discussion:
 *    Invoked when the user enters a monitored region.  This callback will be invoked for every allocated
 *    CLLocationManager instance with a non-nil delegate that implements this method.
 */
- (void)locationManager:(CLLocationManager *)manager
didEnterRegion:(CLRegion *)region __OSX_AVAILABLE_STARTING(__MAC_10_7,__IPHONE_4_0);

/*
 *  locationManager:didExitRegion:
 *
 *  Discussion:
 *    Invoked when the user exits a monitored region.  This callback will be invoked for every allocated
 *    CLLocationManager instance with a non-nil delegate that implements this method.
 */
- (void)locationManager:(CLLocationManager *)manager
didExitRegion:(CLRegion *)region __OSX_AVAILABLE_STARTING(__MAC_10_7,__IPHONE_4_0);

/*
 *  locationManager:monitoringDidFailForRegion:withError:
 *
 *  Discussion:
 *    Invoked when a region monitoring error has occurred. Error types are defined in "CLError.h".
 */
- (void)locationManager:(CLLocationManager *)manager
monitoringDidFailForRegion:(CLRegion *)region
withError:(NSError *)error __OSX_AVAILABLE_STARTING(__MAC_10_7,__IPHONE_4_0);

/*
 *  locationManager:didChangeAuthorizationStatus:
 *
 *  Discussion:
 *    Invoked when the authorization status changes for this application.
 */
- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status __OSX_AVAILABLE_STARTING(__MAC_10_7,__IPHONE_4_2);

/*
 *  locationManager:didStartMonitoringForRegion:
 *
 *  Discussion:
 *    Invoked when a monitoring for a region started successfully.
 */
- (void)locationManager:(CLLocationManager *)manager
didStartMonitoringForRegion:(CLRegion *)region __OSX_AVAILABLE_STARTING(__MAC_TBD,__IPHONE_5_0);

/*
 *  Discussion:
 *    Invoked when location updates are automatically paused.
 */
- (void)locationManagerDidPauseLocationUpdates:(CLLocationManager *)manager __OSX_AVAILABLE_STARTING(__MAC_NA,__IPHONE_6_0);

/*
 *  Discussion:
 *    Invoked when location updates are automatically resumed.
 *
 *    In the event that your application is terminated while suspended, you will
 *	  not receive this notification.
 */
- (void)locationManagerDidResumeLocationUpdates:(CLLocationManager *)manager __OSX_AVAILABLE_STARTING(__MAC_NA,__IPHONE_6_0);

/*
 *  locationManager:didFinishDeferredUpdatesWithError:
 *
 *  Discussion:
 *    Invoked when deferred updates will no longer be delivered. Stopping
 *    location, disallowing deferred updates, and meeting a specified criterion
 *    are all possible reasons for finishing deferred updates.
 *
 *    An error will be returned if deferred updates end before the specified
 *    criteria are met (see CLError).
 */
- (void)locationManager:(CLLocationManager *)manager
didFinishDeferredUpdatesWithError:(NSError *)error __OSX_AVAILABLE_STARTING(__MAC_NA,__IPHONE_6_0);
#endif

@end
